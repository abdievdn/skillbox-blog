package main.api.response;

import lombok.Data;

@Data
public class RegisterErrorsResponse {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
