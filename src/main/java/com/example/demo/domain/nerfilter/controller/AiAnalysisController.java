package com.example.demo.domain.nerfilter.controller;

import com.example.demo.domain.nerfilter.dto.AiResponse;
import com.example.demo.domain.nerfilter.dto.TextRequest;
import com.example.demo.domain.nerfilter.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/filter")
@RequiredArgsConstructor
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    @PostMapping("/check")
    public AiResponse checkText(@RequestBody TextRequest request) {
        // request.text()와 request.fix_spelling()이 포함된 객체가 그대로 서비스로 전달됨
        return aiAnalysisService.getAnalysis(request);
    }
}
