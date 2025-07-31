package com.emailgen.dto;

public record ErrorResponse(String error, String message, String timestamp, String path) {}
