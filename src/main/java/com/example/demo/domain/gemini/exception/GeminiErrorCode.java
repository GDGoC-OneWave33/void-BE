package com.example.demo.domain.gemini.exception;
import com.example.demo.shared.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeminiErrorCode implements BaseCode {

    GEMINI_TOO_MANY_REQUESTS(
            HttpStatus.TOO_MANY_REQUESTS,
            "GEMINI-001",
            "GEMINI 요청이 너무 많습니다. 잠시 후 다시 시도해주세요."
    ),

    GEMINI_API_ERROR(
            HttpStatus.BAD_GATEWAY,
            "GEMINI-002",
            "GEMINI API 호출 중 오류가 발생했습니다."
    ),

    GEMINI_TIMEOUT(
            HttpStatus.GATEWAY_TIMEOUT,
            "GEMINI-003",
            "GEMINI 응답 시간이 초과되었습니다."
    ),

    GEMINI_UNKNOWN_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "GEMINI-004",
            "GEMINI 처리 중 알 수 없는 오류가 발생했습니다."
    ),
    GEMINI_RESPONSE_FORMAT_ERROR(
            HttpStatus.BAD_GATEWAY,
            "GEMINI-005",
            "GEMINI 응답 형식이 올바르지 않습니다."
    ),

    GEMINI_PARSE_ERROR(
            HttpStatus.BAD_GATEWAY,
            "GEMINI-006",
            "GEMINI 응답 JSON 파싱에 실패했습니다."
    ),

    GEMINI_NO_CONTENT(
            HttpStatus.BAD_GATEWAY,
            "GEMINI-007",
            "GEMINI 응답에 아무 내용이 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
