package main.controller.advice;

import main.api.response.post.PostAddEditErrorsResponse;
import main.api.response.post.PostAddEditResponse;
import main.controller.advice.exception.PostAddEditException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PostAddEditAdvice {

    @ExceptionHandler(PostAddEditException.class)
    public ResponseEntity<PostAddEditResponse> handleException(PostAddEditException e) {
        PostAddEditResponse postAddEditResponse = new PostAddEditResponse();
        PostAddEditErrorsResponse postAddEditErrorsResponse = new PostAddEditErrorsResponse();
        switch (e.getPostAddEditError()) {
            case TEXT:
                postAddEditErrorsResponse.setText(e.getMessage());
                break;
            case TITLE:
                postAddEditErrorsResponse.setTitle(e.getMessage());
                break;
            default:
                postAddEditErrorsResponse = null;
                break;
        }
        postAddEditResponse.setResult(false);
        postAddEditResponse.setErrors(postAddEditErrorsResponse);
        return ResponseEntity.ok(postAddEditResponse);
    }
}
