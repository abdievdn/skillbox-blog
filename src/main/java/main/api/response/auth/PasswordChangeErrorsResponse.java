package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordChangeErrorsResponse {
    private String code;
    private String password;
    private String captcha;
}
