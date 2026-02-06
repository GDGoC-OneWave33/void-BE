package com.example.demo.domain.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GptRequest {

    private String model;
    private List<Message> messages;


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
    }

    public static GptRequest of(String systemPrompt, String userPrompt) {
        return GptRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(
                        new Message("system", systemPrompt),
                        new Message("user", userPrompt)
                ))
                .build();
    }
}
