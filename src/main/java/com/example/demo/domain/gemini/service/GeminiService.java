package com.example.demo.domain.gemini.service;

import com.example.demo.domain.gemini.dto.GeminiRequest;
import com.example.demo.domain.gemini.dto.GeminiResponse;
import com.example.demo.domain.gemini.exception.GeminiErrorCode;
import com.example.demo.domain.nerfilter.dto.AiResponse;
import com.example.demo.domain.nerfilter.dto.TextRequest;
import com.example.demo.domain.nerfilter.service.AiAnalysisService;
import com.example.demo.domain.ranking.service.RankingService;
import com.example.demo.shared.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final RankingService rankingService;

    private final AiAnalysisService aiAnalysisService;

    public Map<String, Object> askGemini(String userContent) {
        TextRequest textRequest = new TextRequest(userContent, true);
        AiResponse aiResponse = aiAnalysisService.getAnalysis(textRequest);

        String filteredContent = aiResponse.getFilteredText();
        if (filteredContent == null || filteredContent.isBlank()) {
            throw new CustomException(GeminiErrorCode.GEMINI_NO_CONTENT);
        }


        String cleanKey = apiKey.trim();

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + cleanKey;

        String systemPrompt = "너는 유저의 고민을 듣고 위로해주는 상담사야. " +
                "먼저 유저의 입력이 위로가 필요한 상황인지 판단해. " +
                "거부해야 할 입력: 숫자만 나열, 명백한 테스트 메시지(예: 'test', '테스트', '123'), 봇/스팸 같은 입력, 의미없는 단어 무한 반복. " +
                "위로해줘야 할 입력: 고민/감정 표현, 한글 자음모음을 막 친 것(빡치거나 힘들어서 그럴 수 있음), 욕설이나 분노 표현, 짧은 한숨이나 감정 표현. " +
                "만약 위로가 필요한 상황이라면: 다정하게 위로해주고, 너무 AI 같지 않게 사람답게 말해. " +
                "유저를 절대 비난하지 말고, 무조건적인 공감과 지지를 보내줘. " +
                "단순히 '힘드시겠네요'가 아니라, 유저가 느낄 감정을 구체적인 단어(허탈함, 막막함, 억울함 등)로 묘사해줘." +
                "비슷한 상황이라도 매번 다른 비유나 위로의 문장을 사용해. 때로는 따뜻한 차 한 잔 같은 위로를, 때로는 든든한 내 편이 되어주는 말투를 사용해."+
                "키보드를 막 친 것 같으면 '많이 힘드셨나봐요' 같은 공감을 표현해줘. " +
                "핵심 키워드를 욕설 제외하고 3개 정도 뽑아줘. 감정 키워드도 괜찮아. 갯수에 제한받기보다는 정말 중요한 키워드만 뽑아. " +
                "반드시 JSON 형식으로만 대답해. " +
                "위로할 때: {\"isValid\": true, \"keyword\": [\"단어1\", \"단어2\"], \"answer\": \"위로 내용\"} " +
                "거부할 때: {\"isValid\": false, \"keyword\": [], \"answer\": \"\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequest request = GeminiRequest.of(systemPrompt, filteredContent);
        HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

        try {
            GeminiResponse response = restTemplate.postForObject(url, entity, GeminiResponse.class);

            if (response == null) throw new CustomException(GeminiErrorCode.GEMINI_NO_CONTENT);

            String rawContent = response.getAnswer()
                    .orElseThrow(() -> new CustomException(GeminiErrorCode.GEMINI_NO_CONTENT));

            try {
                String cleanedJson = rawContent.replaceAll("(?s)```json|```", "").trim();
                Map<String, Object> result = objectMapper.readValue(cleanedJson, Map.class);

                // 무의미한 입력인지 확인
                Object isValidObj = result.get("isValid");
                boolean isValid = isValidObj instanceof Boolean ? (Boolean) isValidObj : true;
                if (!isValid) {
                    throw new CustomException(GeminiErrorCode.GEMINI_INVALID_INPUT);
                }

                Object keywordObj = result.get("keyword");
                if (keywordObj instanceof List) {
                    List<String> keywords = (List<String>) keywordObj;
                    for (String kw : keywords) {
                        rankingService.incrementKeywordCount(kw.trim());
                    }
                } else if (keywordObj instanceof String) {
                    String[] splitKeywords = ((String) keywordObj).split(",");
                    for (String kw : splitKeywords) {
                        rankingService.incrementKeywordCount(kw.trim());
                    }
                }

                // isValid 필드 제거 후 반환
                result.remove("isValid");
                return result;
            } catch (JsonProcessingException e) {
                throw new CustomException(GeminiErrorCode.GEMINI_PARSE_ERROR);
            }

        } catch (CustomException e) {
            // CustomException은 그대로 던지기
            throw e;
        } catch (HttpClientErrorException.NotFound e) {
            System.err.println("### 404 에러 상세: " + e.getResponseBodyAsString());
            throw new CustomException(GeminiErrorCode.GEMINI_API_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(GeminiErrorCode.GEMINI_API_ERROR);
        }
    }
}