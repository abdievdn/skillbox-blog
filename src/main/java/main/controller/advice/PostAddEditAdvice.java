package main.controller.advice;

import main.api.response.post.PostAddEditErrorResponse;
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
        PostAddEditErrorResponse postAddEditErrorResponse = new PostAddEditErrorResponse();
        switch (e.getPostAddEditError()) {
            case TEXT:
                postAddEditErrorResponse.setText(e.getMessage());
                break;
            case TITLE:
                postAddEditErrorResponse.setTitle(e.getMessage());
                break;
            default:
                postAddEditErrorResponse = null;
                break;
        }
        postAddEditResponse.setResult(false);
        postAddEditResponse.setErrors(postAddEditErrorResponse);
        return ResponseEntity.ok(postAddEditResponse);
    }
}
