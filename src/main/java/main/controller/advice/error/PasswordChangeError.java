package main.controller.advice.error;

import main.Blog;

public enum PasswordChangeError {
     CODE(Blog.ERROR_MESSAGE_OUTDATED_LINK),
     PASSWORD(Blog.ERROR_MESSAGE_PASSWORD_LENGTH),
     CAPTCHA(Blog.ERROR_MESSAGE_CAPTCHA_CODE);

     private final String error;

     PasswordChangeError(String error) {
          this.error = error;
     }

     public String getError() {
          return error;
     }

}
