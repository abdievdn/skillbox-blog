package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAddEditResponse {
    private boolean result;
    private PostAddEditErrorsResponse errors;
}
