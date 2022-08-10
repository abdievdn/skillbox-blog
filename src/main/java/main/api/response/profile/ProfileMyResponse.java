package main.api.response.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.controller.advice.ErrorsResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileMyResponse {
    private boolean result;
    ErrorsResponse errors;
}
