package main.api.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PasswordChangeRequest {
    @NotNull
    private String code;
    @NotNull
    private String password;
    @NotNull
    private String captcha;
    @NotNull
    @JsonProperty ("captcha_secret")
    private String captchaSecret;
}