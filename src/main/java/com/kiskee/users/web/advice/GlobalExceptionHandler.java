package com.kiskee.users.web.advice;

import com.kiskee.users.exception.DuplicateResourceException;
import com.kiskee.users.exception.RegistrationRestrictionException;
import com.kiskee.users.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String logMessage = "[{}] request has received with [{}] at [{}]";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();

        List<FieldError> fieldErrors = result.getFieldErrors();

        Map<String, String> errors = fieldErrors.stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler({RegistrationRestrictionException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception exception) {
        return handleCustomException(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(Exception exception) {
        return handleCustomException(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(Exception exception) {
        return handleCustomException(exception, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResponse> handleCustomException(Throwable exception, HttpStatus status) {
        String errorMessage = exception.getMessage();

        Map<String, String> errors = Map.of("error", errorMessage);

        return buildErrorResponse(status, errors);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, Map<String, String> errors) {
        Instant timestamp = Instant.now();

        ErrorResponse response = new ErrorResponse(status.getReasonPhrase(), errors, timestamp);

        log.info(logMessage, response.status(), errors, timestamp);

        return ResponseEntity.status(status).body(response);
    }
}
