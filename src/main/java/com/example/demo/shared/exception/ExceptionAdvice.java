package com.example.demo.shared.exception;


import com.example.demo.shared.response.ApiResponse;
import com.example.demo.shared.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // @Valid @RequestBody DTO 검증 실패
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            String msg = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
            errors.merge(field, msg, (a, b) -> a + ", " + b);
        });

        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorCode.INVALID_REQUEST,
                errors
        );
        return handleExceptionInternal(e, body, headers,
                ErrorCode.INVALID_REQUEST.getHttpStatus(),
                request);
    }

    // 도메인 CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e, HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(e.getErrorCode(),null);
        WebRequest webRequest = new ServletWebRequest(request);
        return handleExceptionInternal(e, body, new HttpHeaders(), e.getErrorCode().getHttpStatus(), webRequest);
    }

    // 모든 미처리 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception e, WebRequest request) {
        log.error("Unhandled exception", e); // printStackTrace() 지양
        ApiResponse<Object> body = ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR, null);
        return handleExceptionInternal(e, body, new HttpHeaders(),
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(), request);
    }


    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<ApiResponse<Object>> handleInvalidDateFormat(InvalidFormatException ex) {
        if (ex.getTargetType() == LocalDate.class) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.onFailure(ErrorCode.INVALID_DATE, null)
            );
        }
        return ResponseEntity.badRequest().body(
                ApiResponse.onFailure(ErrorCode.INVALID_FORMAT, null)
        );
    }



}
