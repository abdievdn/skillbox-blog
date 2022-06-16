package main.controller.advice.error;

public enum ImageUploadError {
    IMAGE("Размер файла превышает допустимый размер"),
    PHOTO("Фото слишком большое, нужно не более 5 Мб");

    private final String error;


    ImageUploadError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
