package com.example.demo.domain.ranking.controller;

import com.example.demo.domain.ranking.service.RankingService;
import com.example.demo.shared.response.ApiResponse;
import com.example.demo.shared.response.SuccessCode;
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

    @GetMapping("/top3")
    public ApiResponse<List<Map<String, Object>>> getTop3() {
        List<Map<String, Object>> result = rankingService.getTop3WithPercentage();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}