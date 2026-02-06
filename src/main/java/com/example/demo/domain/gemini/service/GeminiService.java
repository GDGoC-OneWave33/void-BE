package com.example.demo.domain.gemini.service;

import com.example.demo.domain.gemini.dto.GeminiRequest;
import com.example.demo.domain.gemini.dto.GeminiResponse;
import com.example.demo.domain.gemini.exception.GeminiErrorCode;
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

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public Map<String, Object> askGemini(String userContent) {
        String cleanKey = apiKey.trim();

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + cleanKey;

        String systemPrompt = "유저의 고민을 듣고 다정하게 위로해줘. 핵심 키워드 3개 정도를 욕설 제외하고 뽑아줘. 갯수에 제한 받기 보다는, 정말 중요하다 생각되는 키워드를 뽑아."
                + "반드시 JSON 형식으로만 대답해. 형식: {\"keyword\": \"단어\", \"answer\": \"내용\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequest request = GeminiRequest.of(systemPrompt, userContent);
        HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

        try {
            GeminiResponse response = restTemplate.postForObject(url, entity, GeminiResponse.class);

            if (response == null) throw new CustomException(GeminiErrorCode.GEMINI_NO_CONTENT);

            String rawContent = response.getAnswer()
                    .orElseThrow(() -> new CustomException(GeminiErrorCode.GEMINI_NO_CONTENT));

            try {
                String cleanedJson = rawContent.replaceAll("(?s)```json|```", "").trim();
                return objectMapper.readValue(cleanedJson, Map.class);
            } catch (JsonProcessingException e) {
                throw new CustomException(GeminiErrorCode.GEMINI_PARSE_ERROR);
            }

        } catch (HttpClientErrorException.NotFound e) {
            System.err.println("### 404 에러 상세: " + e.getResponseBodyAsString());
            throw new CustomException(GeminiErrorCode.GEMINI_API_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(GeminiErrorCode.GEMINI_API_ERROR);
        }
    }
}