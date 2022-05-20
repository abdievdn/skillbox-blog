package main.controller.advice.error;

public enum PostAddEditError {
     TITLE("Заголовок не установлен"),
     TEXT("Текст публикации слишком короткий");

    private final String error;

    PostAddEditError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
