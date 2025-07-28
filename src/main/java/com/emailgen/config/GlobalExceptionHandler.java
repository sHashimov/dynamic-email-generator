package com.emailgen.config;

import com.emailgen.dto.ErrorResponse;
import com.emailgen.exception.ExpressionEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ExpressionEvaluationException.class)
    public ResponseEntity<ErrorResponse> handleExpressionErrors(ExpressionEvaluationException e) {
        return buildErrorResponse("ExpressionError", e.getMessage(), 400);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return buildErrorResponse("BadRequest", e.getMessage(), 400);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrors(Exception e) {
        LOG.error("Unhandled exception occurred", e);
        return buildErrorResponse("InternalServerError", "Something went wrong. Please contact support.", 500);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String errorType, String message, int status) {
        ErrorResponse error = new ErrorResponse(errorType, message);
        return ResponseEntity.status(status).body(error);
    }
}
