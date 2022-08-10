package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.controller.advice.ErrorsResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordChangeResponse {
    private boolean result;
    private ErrorsResponse errors;
}
