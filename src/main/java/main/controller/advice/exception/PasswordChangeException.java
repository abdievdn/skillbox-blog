package main.controller.advice.exception;

import lombok.Data;
import main.controller.advice.error.PasswordChangeError;

@Data
public class PasswordChangeException extends Exception{

    private final PasswordChangeError passwordChangeError;

    @Override
    public String getMessage() {
        return passwordChangeError.getError();
    }
}
