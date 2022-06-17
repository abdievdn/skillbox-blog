package main.controller.advice;

import main.api.response.general.PostCommentAddErrorsResponse;
import main.api.response.general.PostCommentAddResponse;
import main.controller.advice.error.PostCommentAddError;
import main.controller.advice.exception.PostCommentAddException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PostCommentAddAdvice {

    @ExceptionHandler(PostCommentAddException.class)
    public ResponseEntity<PostCommentAddResponse> handleException(PostCommentAddException e) {
        PostCommentAddResponse postCommentAddResponse = new PostCommentAddResponse();
        PostCommentAddErrorsResponse postCommentAddErrorsResponse = new PostCommentAddErrorsResponse();
        if (e.getPostCommentAddError() == PostCommentAddError.TEXT) {
            postCommentAddErrorsResponse.setText(e.getMessage());
        }
        postCommentAddResponse.setResult(false);
        postCommentAddResponse.setErrors(postCommentAddErrorsResponse);
        return new ResponseEntity<>(postCommentAddResponse, HttpStatus.BAD_REQUEST);
    }
}
