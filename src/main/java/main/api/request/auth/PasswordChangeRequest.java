package main.api.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String code;
    private String password;
    private String captcha;
    @JsonProperty ("captcha_secret")
    private String captchaSecret;
}
