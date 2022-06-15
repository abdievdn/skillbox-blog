package main.api.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {

    @JsonProperty("e_mail")
    private String email;
    private String password;
    private String name;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;

}
