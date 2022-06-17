package main.controller.advice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.controller.advice.error.PasswordChangeError;

@AllArgsConstructor
@Getter
public class PasswordChangeException extends Exception{

    private final PasswordChangeError passwordChangeError;

    @Override
    public String getMessage() {
        return passwordChangeError.getError();
    }
}
