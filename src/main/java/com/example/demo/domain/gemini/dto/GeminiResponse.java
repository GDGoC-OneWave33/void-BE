package com.example.demo.domain.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponse {
    private List<Candidate> candidates;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Candidate {
        private Content content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part {
        private String text;
    }

    public Optional<String> getAnswer() {
        return Optional.ofNullable(candidates)
                .filter(c -> !c.isEmpty())
                .map(c -> c.get(0).getContent())
                .filter(content -> content.getParts() != null && !content.getParts().isEmpty())
                .map(content -> content.getParts().get(0).getText());
    }
}