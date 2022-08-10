package main.controller.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorsResponseException extends Exception {

    private final ErrorsNum errors;

    @Override
    public String getMessage() {
        return errors.name().toLowerCase() + ":" + errors.getError();
    }
}
