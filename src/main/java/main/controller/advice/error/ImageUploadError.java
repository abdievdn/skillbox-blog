package main.controller.advice.error;

public enum ImageUploadError {
    IMAGE("Размер файла превышает допустимый размер");

    private final String error;


    ImageUploadError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
