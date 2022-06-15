package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private boolean result;
    private UserResponse user;

}
