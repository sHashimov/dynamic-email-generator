package com.emailgen.config;

import com.emailgen.dto.ErrorResponse;
import com.emailgen.exception.BadRequestException;
import com.emailgen.exception.ExpressionEvaluationException;
import com.emailgen.exception.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e, HttpServletRequest request) {
        LOG.warn("Bad request: {}", e.getMessage());
        return buildErrorResponse("BadRequest", e.getMessage(), request.getRequestURI(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpressionEvaluationException.class)
    public ResponseEntity<ErrorResponse> handleExpressionErrors(ExpressionEvaluationException e, HttpServletRequest request) {
        LOG.warn("Expression evaluation error: {}", e.getMessage());
        return buildErrorResponse("ExpressionError", e.getMessage(), request.getRequestURI(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return buildErrorResponse("ValidationError", message, request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException e, HttpServletRequest request) {
        LOG.warn("Invalid JSON in request: {}", e.getMessage());
        return buildErrorResponse("MalformedJson", "Request body is not readable or malformed.",
            request.getRequestURI(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest request) {
        LOG.warn("Invalid credentials: {}", e.getMessage());
        return buildErrorResponse("Unauthorized", e.getMessage(), request.getRequestURI(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrors(Exception e, HttpServletRequest request) {
        LOG.error("Unhandled exception occurred", e);
        return buildErrorResponse("InternalServerError", "Something went wrong. Please contact support.",
            request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String errorType, String message, String path, HttpStatus status) {
        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        ErrorResponse error = new ErrorResponse(errorType, message, timestamp, path);
        return ResponseEntity.status(status).body(error);
    }
}
