package main.controller.advice;

public enum RegisterError {
    EMAIL("Этот e-mail уже зарегистрирован"),
    NAME("Имя указано неверно"),
    PASSWORD("Пароль короче 6-ти символов"),
    CAPTCHA("Код с картинки введён неверно");

    private final String error;

    RegisterError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
