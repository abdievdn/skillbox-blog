package main.controller.advice.error;

import main.Blog;

public enum PostAddEditError {
     TITLE(Blog.ERROR_MESSAGE_POST_TITLE_IS_MISSING),
     TEXT(Blog.ERROR_MESSAGE_POST_TEXT_LENGTH);

    private final String error;

    PostAddEditError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
