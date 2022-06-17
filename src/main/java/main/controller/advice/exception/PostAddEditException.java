package main.controller.advice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.controller.advice.error.PostAddEditError;

@AllArgsConstructor
@Getter
public class PostAddEditException extends Exception {

    private final PostAddEditError postAddEditError;

    @Override
    public String getMessage() {
        return postAddEditError.getError();
    }
}
