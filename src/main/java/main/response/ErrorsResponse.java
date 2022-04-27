package main.response;

public class ErrorsResponse {
    private String email = "Этот e-mail уже зарегистрирован";
    private String name = "Имя указано неверно";
    private String password = "Пароль короче 6-ти символов";
    private String captcha = "Код с картинки введён неверно";
}
