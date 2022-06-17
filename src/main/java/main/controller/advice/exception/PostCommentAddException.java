package main.controller.advice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.controller.advice.error.PostCommentAddError;

@AllArgsConstructor
@Getter
public class PostCommentAddException extends Exception {

    private final PostCommentAddError postCommentAddError;

    @Override
    public String getMessage() {
        return postCommentAddError.getError();
    }
}
