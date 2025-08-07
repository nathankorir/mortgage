package com.mortgage.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class CustomExceptionHandler {
//    @ExceptionHandler(NoSuchElementException.class)
//    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().errorCode(500).path(request.getRequestURI()).detail(ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(MissingResourceException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(MissingResourceException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().errorCode(404).path(request.getRequestURI()).detail(ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().errorCode(400).path(request.getRequestURI()).detail(ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

//    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
//    public ResponseEntity<ErrorResponseDto> handleHttpClientErrorException(BadRequestException ex, HttpServletRequest request) {
//        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().errorCode(403).path(request.getRequestURI()).detail(ex.getMessage()).build();
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDto);
//    }

//
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<?> handleInvalidUUID(MethodArgumentTypeMismatchException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        return ResponseEntity.badRequest().body(errors);
//    }
}
