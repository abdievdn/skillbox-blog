package main.api.response.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.api.response.BlogResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileMyResponse implements BlogResponse {
    private boolean result;
    ProfileMyErrorsResponse errors;
}
