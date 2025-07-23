package com.sundeep.user.user_service.exceptions;

import com.sundeep.user.user_service.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailExists(EmailAlreadyExistsException ex, WebRequest request) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                request.getDescription(false),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDTO,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                request.getDescription(false),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleException(Exception ex) {
//        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                request.getDescription(false),
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }
    // Other handlers...
}