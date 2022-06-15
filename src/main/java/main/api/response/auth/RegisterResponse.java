package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {

    private boolean result;
    private RegisterErrorResponse errors;
}
