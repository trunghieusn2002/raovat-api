package com.raovat.api.config.exception;

public record RestError(int status, String error, String message, String path) {
}
