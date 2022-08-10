package main.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException() {
        ErrorsResponse imageErrorsResponse = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put(ErrorsNum.IMAGE.name().toLowerCase(), ErrorsNum.IMAGE.getError());
        errors.put(ErrorsNum.PHOTO.name().toLowerCase(), ErrorsNum.PHOTO.getError());
        imageErrorsResponse.setErrors(errors);
        imageErrorsResponse.setResult(false);
        return ResponseEntity.ok(imageErrorsResponse);
    }
}