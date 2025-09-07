package com.devicesapi.infrastructure.web.controllers.exception;

import com.devicesapi.domain.exception.DeviceBadRequestException;
import com.devicesapi.domain.exception.DeviceBusinessException;
import com.devicesapi.domain.exception.DeviceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDeviceNotFound(DeviceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", Instant.now(),
                "error", "Device not found",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now(),
                "error", "Internal server error",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(DeviceBadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleDeviceBadRequest(DeviceBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", Instant.now(),
                "error", "Bad Request error",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(DeviceBusinessException.class)
    public ResponseEntity<Map<String, Object>> handleDeviceBusiness(DeviceBusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "timestamp", Instant.now(),
                "error", "Business error",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", Instant.now(),
                "error", "Validation failed",
                "message", "Invalid request data"
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "timestamp", Instant.now(),
                "error", ex.getStatusCode().toString(),
                "message", ex.getReason() != null ? ex.getReason() : "Request failed"
        ));
    }
}
