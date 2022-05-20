package main.controller.advice.error;

public enum ProfileMyError {
    EMAIL("Этот e-mail уже зарегистрирован"),
    PHOTO("Фото слишком большое, нужно не более 5 Мб"),
    NAME("Имя указано неверно"),
    PASSWORD("Пароль короче 6-ти символов");

    private final String error;

    ProfileMyError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
