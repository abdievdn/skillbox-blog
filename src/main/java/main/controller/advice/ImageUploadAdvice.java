package main.controller.advice;

import main.api.response.general.ImageErrorsResponse;
import main.api.response.general.ImageResponse;
import main.controller.advice.exception.ImageUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ImageUploadAdvice {

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ImageResponse> handleException(ImageUploadException e) {
        ImageResponse imageResponse = new ImageResponse();
        ImageErrorsResponse imageErrorsResponse = new ImageErrorsResponse();
        switch (e.getImageUploadError()) {
            case IMAGE:
                imageErrorsResponse.setImage(e.getMessage());
                break;
            case PHOTO:
                imageErrorsResponse.setPhoto(e.getMessage());
                break;
            default: break;
        }
        imageResponse.setResult(false);
        imageResponse.setErrors(imageErrorsResponse);
        return ResponseEntity.ok(imageResponse);
    }
}
