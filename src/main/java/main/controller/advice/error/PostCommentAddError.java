package main.controller.advice.error;

import main.Blog;

public enum PostCommentAddError {
    TEXT(Blog.ERROR_MESSAGE_COMMENT_TEXT_LENGTH);

    private final String error;

    PostCommentAddError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
