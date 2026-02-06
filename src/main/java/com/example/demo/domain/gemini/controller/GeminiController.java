package com.example.demo.domain.gemini.controller;

import com.example.demo.domain.gemini.service.GeminiService;
import com.example.demo.shared.exception.CustomException;
import com.example.demo.shared.response.ApiResponse;
import com.example.demo.shared.response.ErrorCode;
import com.example.demo.shared.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

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
