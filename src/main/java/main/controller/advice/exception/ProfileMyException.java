package main.controller.advice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.controller.advice.error.ProfileMyError;

@AllArgsConstructor
@Getter
public class ProfileMyException extends Exception {

    private final ProfileMyError profileMyError;

    @Override
    public String getMessage() {
        return profileMyError.getError();
    }
}
