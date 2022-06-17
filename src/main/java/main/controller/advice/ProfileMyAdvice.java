package main.controller.advice;

import main.api.response.general.ProfileMyErrorsResponse;
import main.api.response.general.ProfileMyResponse;
import main.controller.advice.exception.ProfileMyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class ProfileMyAdvice {

    @ExceptionHandler({ProfileMyException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<ProfileMyResponse> handleException(ProfileMyException e) {
        ProfileMyResponse profileMyResponse = new ProfileMyResponse();
        ProfileMyErrorsResponse profileMyErrorResponse = new ProfileMyErrorsResponse();
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
