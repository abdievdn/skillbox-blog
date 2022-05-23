package main.controller.advice;

import main.api.response.ImageErrorResponse;
import main.api.response.ImageResponse;
import main.controller.advice.exception.ImageUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ImageUploadAdvice {

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ImageResponse> handleException(ImageUploadException e) {
        ImageResponse imageResponse = new ImageResponse();
        ImageErrorResponse imageErrorResponse = new ImageErrorResponse();
        switch (e.getImageUploadError()) {
            case IMAGE:
                imageErrorResponse.setImage(e.getMessage());
                break;
            default: break;
        }
        imageResponse.setResult(false);
        imageResponse.setErrors(imageErrorResponse);
        return ResponseEntity.ok(imageResponse);
    }
}
