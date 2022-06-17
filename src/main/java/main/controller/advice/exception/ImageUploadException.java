package main.controller.advice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.controller.advice.error.ImageUploadError;

@AllArgsConstructor
@Getter
public class ImageUploadException extends Exception {

    private final ImageUploadError imageUploadError;

    @Override
    public String getMessage() {
        return imageUploadError.getError();
    }
}
