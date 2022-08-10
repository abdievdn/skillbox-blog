package main.controller.advice;

import main.Blog;

public enum ErrorsNum {
    EMAIL(Blog.ERROR_MESSAGE_EMAIL_REGISTERED),
    NAME(Blog.ERROR_MESSAGE_NAME_IS_INCORRECT),
    PASSWORD(Blog.ERROR_MESSAGE_PASSWORD_LENGTH),
    CAPTCHA(Blog.ERROR_MESSAGE_CAPTCHA_CODE),
    CODE(Blog.ERROR_MESSAGE_OUTDATED_LINK),
    PHOTO(Blog.ERROR_MESSAGE_PHOTO_SIZE_EXCEEDED),
    IMAGE(Blog.ERROR_MESSAGE_IMAGE_SIZE_EXCEEDED),
    TITLE(Blog.ERROR_MESSAGE_POST_TITLE_IS_MISSING),
    TEXT(Blog.ERROR_MESSAGE_POST_TEXT_LENGTH),
    COMMENT_TEXT(Blog.ERROR_MESSAGE_COMMENT_TEXT_LENGTH);
    private final String error;

    ErrorsNum(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
