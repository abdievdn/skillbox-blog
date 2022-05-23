package main.controller.advice.exception;

import lombok.Data;
import main.controller.advice.error.ImageUploadError;

@Data
public class ImageUploadException extends Exception {

    private final ImageUploadError imageUploadError;

    @Override
    public String getMessage() {
        return imageUploadError.getError();
    }
}
