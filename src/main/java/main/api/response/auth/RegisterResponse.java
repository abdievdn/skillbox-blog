package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.api.response.BlogResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse implements BlogResponse {
    private boolean result;
    private RegisterErrorsResponse errors;
}
