package com.example.demo.domain.ranking.controller;

import com.example.demo.domain.ranking.service.RankingService;
import com.example.demo.shared.response.ApiResponse;
import com.example.demo.shared.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @Operation(
            summary = "TOP3 키워드 반환",
            description = "TOP3 키워드를 반환합니다."
    )
    @GetMapping("/top3")
    public ApiResponse<List<Map<String, Object>>> getTop3() {
        List<Map<String, Object>> result = rankingService.getTop3WithPercentage();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}