package main.controller.advice;

import main.api.response.auth.RegisterErrorsResponse;
import main.api.response.auth.RegisterResponse;
import main.controller.advice.exception.RegisterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RegisterAdvice {

    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<RegisterResponse> handleException(RegisterException e) {
        RegisterResponse registerResponse = new RegisterResponse();
        RegisterErrorsResponse registerErrorsResponse = new RegisterErrorsResponse();
        switch (e.getRegisterError()) {
            case EMAIL:
                registerErrorsResponse.setEmail(e.getMessage());
                break;
            case CAPTCHA:
                registerErrorsResponse.setCaptcha(e.getMessage());
                break;
            case NAME:
                registerErrorsResponse.setName(e.getMessage());
                break;
            case PASSWORD:
                registerErrorsResponse.setPassword(e.getMessage());
                break;
            default:
                registerErrorsResponse = null;
                break;
        }
        registerResponse.setResult(false);
        registerResponse.setErrors(registerErrorsResponse);
        return ResponseEntity.ok(registerResponse);
    }
}
