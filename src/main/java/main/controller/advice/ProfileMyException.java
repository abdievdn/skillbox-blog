package main.controller.advice;

import lombok.Data;

@Data
public class ProfileMyException extends Exception {

    private final ProfileMyError profileMyError;

    @Override
    public String getMessage() {
        return profileMyError.getError();
    }
}
