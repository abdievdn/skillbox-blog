package main.controller.advice.error;

public enum PostCommentAddError {
    TEXT("Текст комментария не задан или слишком короткий");

    private final String error;

    PostCommentAddError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
