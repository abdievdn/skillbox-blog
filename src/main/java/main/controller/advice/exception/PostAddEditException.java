package main.controller.advice.exception;

import lombok.Data;
import main.controller.advice.error.PostAddEditError;

@Data
public class PostAddEditException extends Exception {

    private final PostAddEditError postAddEditError;

    @Override
    public String getMessage() {
        return postAddEditError.getError();
    }
}
