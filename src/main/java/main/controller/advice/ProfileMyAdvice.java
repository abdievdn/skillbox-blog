package main.controller.advice;

import main.api.response.ProfileMyErrorResponse;
import main.api.response.ProfileMyResponse;
import main.controller.advice.exception.ProfileMyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProfileMyAdvice {

    @ExceptionHandler(ProfileMyException.class)
    public ResponseEntity<ProfileMyResponse> handleException(ProfileMyException e) {
        ProfileMyResponse profileMyResponse = new ProfileMyResponse();
        ProfileMyErrorResponse profileMyErrorResponse = new ProfileMyErrorResponse();
        switch (e.getProfileMyError()) {
            case EMAIL:
                profileMyErrorResponse.setEmail(e.getMessage());
                break;
            case PHOTO:
                profileMyErrorResponse.setPhoto(e.getMessage());
                break;
            case NAME:
                profileMyErrorResponse.setName(e.getMessage());
                break;
            case PASSWORD:
                profileMyErrorResponse.setPassword(e.getMessage());
                break;
            default:
                profileMyErrorResponse = null;
                break;
        }
        profileMyResponse.setResult(false);
        profileMyResponse.setErrors(profileMyErrorResponse);
        return ResponseEntity.ok(profileMyResponse);
    }
}
