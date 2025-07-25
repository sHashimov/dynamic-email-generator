package com.emailgen.config;

import com.emailgen.util.ExpressionEvaluationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpressionEvaluationException.class)
    public ResponseEntity<?> handleExpressionErrors(ExpressionEvaluationException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpectedErrors(Exception e) {
        return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
    }
}
