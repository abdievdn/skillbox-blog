package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterErrorsResponse {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
