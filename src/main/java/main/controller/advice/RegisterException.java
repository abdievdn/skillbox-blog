package main.controller.advice;

import lombok.Data;

@Data
public class RegisterException extends Exception {

    private final RegisterError registerError;

    @Override
    public String getMessage() {
        return registerError.getError();
    }
}
