package com.example.demo.domain.nerfilter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TextRequest {

        @JsonProperty("text")
        private String text;

        @JsonProperty("fix_spelling")
        private boolean fixSpelling = true; // 자바 관례대로 camelCase를 쓰고 JSON 이름만 지정
}