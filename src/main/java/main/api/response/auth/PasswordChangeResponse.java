package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.api.response.BlogResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordChangeResponse implements BlogResponse {
    private boolean result;
    private PasswordChangeErrorsResponse errors;
}
