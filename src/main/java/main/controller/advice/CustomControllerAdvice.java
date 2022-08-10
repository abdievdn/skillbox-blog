package main.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomControllerAdvice {
    @ExceptionHandler(ErrorsResponseException.class)
    public ResponseEntity<ErrorsResponse> handleException(Exception e) {
        ErrorsResponse registerResponse = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        String message = e.getMessage();
        errors.put(message.substring(0, message.indexOf(":")) , message.substring(message.indexOf(":") + 1));
        registerResponse.setErrors(errors);
        registerResponse.setResult(false);
        return ResponseEntity.ok(registerResponse);
    }
}