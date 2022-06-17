package main.controller.advice.error;

import main.Blog;

public enum ImageUploadError {
    IMAGE(Blog.ERROR_MESSAGE_IMAGE_SIZE_EXCEEDED),
    PHOTO(Blog.ERROR_MESSAGE_PHOTO_SIZE_EXCEEDED);

    private final String error;

    ImageUploadError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
