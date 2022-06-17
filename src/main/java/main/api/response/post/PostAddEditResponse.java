package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.api.response.BlogResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAddEditResponse implements BlogResponse {
    private boolean result;
    private PostAddEditErrorsResponse errors;
}
