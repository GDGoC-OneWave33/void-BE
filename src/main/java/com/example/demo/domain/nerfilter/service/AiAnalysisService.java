package com.example.demo.domain.nerfilter.service;

import com.example.demo.domain.nerfilter.dto.AiResponse;
import com.example.demo.domain.nerfilter.dto.TextRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Service
public class AiAnalysisService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://13.209.148.142:80";

    public AiAnalysisService() {
        this.restTemplate = new RestTemplate();
    }

    public AiResponse getAnalysis(TextRequest text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TextRequest> request = new HttpEntity<>(text, headers);

        return restTemplate.postForObject(
                baseUrl + "/filter",
                request,
                AiResponse.class
        );
    }
}