package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Integer id;
    private String name;
    private String photo;
    private String email;
    private Boolean moderation;
    private Integer moderationCount;
    private Boolean settings;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
