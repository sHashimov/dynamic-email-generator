package com.emailgen.config;

import com.emailgen.dto.ErrorResponse;
import com.emailgen.exception.ExpressionEvaluationException;
import com.emailgen.exception.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ExpressionEvaluationException.class)
    public ResponseEntity<ErrorResponse> handleExpressionErrors(ExpressionEvaluationException e) {
        LOG.warn("Expression evaluation error: {}", e.getMessage());
        return buildErrorResponse("ExpressionError", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        LOG.warn("Illegal argument: {}", e.getMessage());
        return buildErrorResponse("BadRequest", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        LOG.warn("Invalid credentials: {}", e.getMessage());
        return buildErrorResponse("Unauthorized", e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrors(Exception e) {
        LOG.error("Unhandled exception occurred", e);
        return buildErrorResponse("InternalServerError", "Something went wrong. Please contact support.",
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String errorType, String message, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(errorType, message);
        return ResponseEntity.status(status).body(error);
    }
}
