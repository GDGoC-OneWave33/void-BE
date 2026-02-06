package com.example.demo.domain.gemini.controller;

import com.example.demo.domain.gemini.service.GeminiService;
import com.example.demo.shared.exception.CustomException;
import com.example.demo.shared.response.ApiResponse;
import com.example.demo.shared.response.ErrorCode;
import com.example.demo.shared.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @Operation(
            summary = "감정 배출 및 키워드 추출",
            description = "유저의 감정(content)을 보내면 gemini가 답변과 핵심 키워드를 반환합니다."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "상담할 고민 내용",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"content\": \"오늘 해커톤 너무 힘들다.\"}"),
                    examples = @ExampleObject(
                            name = "고민 상담 예시",
                            value = "{\"content\": \"해커톤 프로젝트가 생각보다 어려워서 속상해...\"}"
                    )
            )
    )
    @PostMapping("/ask")
    public ApiResponse<Map<String, Object>> ask(@RequestBody Map<String, String> request) {
        String userContent = request.get("content");

        if (userContent == null || userContent.isBlank()) {
            return ApiResponse.onSuccess(
                    Map.of("answer", "내용을 입력해 주세요!", "keyword", "입력 필요"),
                    SuccessCode.OK
            );
        }

        Map<String, Object> result = geminiService.askGemini(userContent);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
