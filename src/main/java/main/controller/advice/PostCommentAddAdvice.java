package main.controller.advice;

import main.api.response.post.PostCommentAddErrorResponse;
import main.api.response.post.PostCommentAddResponse;
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
        PostCommentAddErrorResponse postCommentAddErrorResponse = new PostCommentAddErrorResponse();
        switch(e.getPostCommentAddError()) {
            case TEXT:
                postCommentAddErrorResponse.setText(e.getMessage());
                break;
            default: break;
        }
        postCommentAddResponse.setResult(false);
        postCommentAddResponse.setErrors(postCommentAddErrorResponse);
        return new ResponseEntity<>(postCommentAddResponse, HttpStatus.BAD_REQUEST);
    }
}
