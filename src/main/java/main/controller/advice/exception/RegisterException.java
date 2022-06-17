package main.controller.advice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.controller.advice.error.RegisterError;

@AllArgsConstructor
@Getter
public class RegisterException extends Exception {

    private final RegisterError registerError;

    @Override
    public String getMessage() {
        return registerError.getError();
    }
}
