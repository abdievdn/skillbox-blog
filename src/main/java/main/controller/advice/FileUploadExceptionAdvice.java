package main.controller.advice;

import main.api.response.BlogResponse;
import main.api.response.general.ImageErrorsResponse;
import main.api.response.general.ImageResponse;
import main.controller.advice.error.ImageUploadError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BlogResponse> handleMaxSizeException() {
        ImageResponse imageResponse = new ImageResponse();
        ImageErrorsResponse imageErrorsResponse = new ImageErrorsResponse();
        imageErrorsResponse.setImage(ImageUploadError.IMAGE.getError());
        imageErrorsResponse.setPhoto(ImageUploadError.PHOTO.getError());
        imageResponse.setErrors(imageErrorsResponse);
        imageResponse.setResult(false);
        return ResponseEntity.ok(imageResponse);
    }
}
