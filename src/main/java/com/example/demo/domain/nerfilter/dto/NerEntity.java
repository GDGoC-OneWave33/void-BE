package com.example.demo.domain.nerfilter.dto;

public record NerEntity(
        String entity,
        double score,
        String word,
        int start,
        int end
) {}
