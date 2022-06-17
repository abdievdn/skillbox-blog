package main.controller.advice.error;

import main.Blog;

public enum RegisterError {
    EMAIL(Blog.ERROR_MESSAGE_EMAIL_REGISTERED),
    NAME(Blog.ERROR_MESSAGE_NAME_IS_INCORRECT),
    PASSWORD(Blog.ERROR_MESSAGE_PASSWORD_LENGTH),
    CAPTCHA(Blog.ERROR_MESSAGE_CAPTCHA_CODE);

    private final String error;

    RegisterError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
