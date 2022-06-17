package main.controller.advice;

import main.api.response.auth.PasswordChangeErrorsResponse;
import main.api.response.auth.PasswordChangeResponse;
import main.controller.advice.exception.PasswordChangeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PasswordChangeAdvice {

    @ExceptionHandler(PasswordChangeException.class)
    public ResponseEntity<PasswordChangeResponse> handleException(PasswordChangeException e) {
        PasswordChangeResponse passwordChangeResponse = new PasswordChangeResponse();
        PasswordChangeErrorsResponse passwordChangeErrorsResponse = new PasswordChangeErrorsResponse();
        switch (e.getPasswordChangeError()) {
            case CODE:
                passwordChangeErrorsResponse.setCode(e.getMessage());
                break;
            case PASSWORD:
                passwordChangeErrorsResponse.setPassword(e.getMessage());
                break;
            case CAPTCHA:
                passwordChangeErrorsResponse.setCaptcha(e.getMessage());
                break;
            default: break;
        }
        passwordChangeResponse.setResult(false);
        passwordChangeResponse.setErrors(passwordChangeErrorsResponse);
        return ResponseEntity.ok(passwordChangeResponse);
    }
}
