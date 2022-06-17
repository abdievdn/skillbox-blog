package main.controller.advice.error;

import main.Blog;

public enum ProfileMyError {
    EMAIL(Blog.ERROR_MESSAGE_EMAIL_REGISTERED),
    PHOTO(Blog.ERROR_MESSAGE_PHOTO_SIZE_EXCEEDED),
    NAME(Blog.ERROR_MESSAGE_NAME_IS_INCORRECT),
    PASSWORD(Blog.ERROR_MESSAGE_PASSWORD_LENGTH);

    private final String error;

    ProfileMyError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
