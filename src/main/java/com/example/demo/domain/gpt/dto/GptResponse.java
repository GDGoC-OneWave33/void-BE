package com.example.demo.domain.gpt.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

import java.awt.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GptResponse {
    private List<Choice> choices;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Choice {
        private Message message;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
    }

    public String getAnswer() {
        if (this.choices == null || this.choices.isEmpty()) {
            return "GPT가 대답을 못 했어..";
        }
        return this.choices.get(0).getMessage().getContent();
    }


}
