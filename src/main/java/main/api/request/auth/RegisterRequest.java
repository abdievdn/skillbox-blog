package main.api.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegisterRequest {
    @NotNull
    @JsonProperty("e_mail")
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String captcha;
    @NotNull
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}