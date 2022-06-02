package main.controller.advice.exception;

import lombok.Data;
import main.controller.advice.error.PostCommentAddError;

@Data
public class PostCommentAddException extends Exception {

    private final PostCommentAddError postCommentAddError;

    @Override
    public String getMessage() {
        return postCommentAddError.getError();
    }
}
