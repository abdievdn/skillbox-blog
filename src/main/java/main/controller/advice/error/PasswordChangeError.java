package main.controller.advice.error;

public enum PasswordChangeError {
     CODE("Ссылка для восстановления пароля устарела. <a href=\"../restore-password\">Запросить ссылку снова</a>"),
     PASSWORD("Пароль короче 6-ти символов"),
     CAPTCHA("Код с картинки введён неверно");

     private final String error;

     PasswordChangeError(String error) {
          this.error = error;
     }

     public String getError() {
          return error;
     }

}
