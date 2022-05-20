package main.controller.advice.exception;

import lombok.Data;
import main.controller.advice.error.ProfileMyError;

@Data
public class ProfileMyException extends Exception {

    private final ProfileMyError profileMyError;

    @Override
    public String getMessage() {
        return profileMyError.getError();
    }
}
