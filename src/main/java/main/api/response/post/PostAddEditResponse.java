package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.controller.advice.ErrorsResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAddEditResponse {
    private boolean result;
    private ErrorsResponse errors;
}
