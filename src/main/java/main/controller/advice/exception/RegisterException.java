package main.controller.advice.exception;

import lombok.Data;
import main.controller.advice.error.RegisterError;

@Data
public class RegisterException extends Exception {

    private final RegisterError registerError;

    @Override
    public String getMessage() {
        return registerError.getError();
    }
}
