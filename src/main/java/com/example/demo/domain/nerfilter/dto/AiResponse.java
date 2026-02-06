package com.example.demo.domain.nerfilter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {

    @JsonProperty("original_text")
    private String originalText;

    @JsonProperty("corrected_text")
    private String correctedText;

    @JsonProperty("filtered_text")
    private String filteredText;

    @JsonProperty("detected_entities")
    private List<Map<String, Object>> detectedEntities;  // ✅ Object로 변경

    @JsonProperty("spelling_errors")
    private List<Map<String, Object>> spellingErrors;  // ✅ Object로 변경
}
