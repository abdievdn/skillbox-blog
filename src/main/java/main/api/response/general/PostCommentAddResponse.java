package main.api.response.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.api.response.BlogResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostCommentAddResponse implements BlogResponse {
    private Integer id;
    private Boolean result;
    private PostCommentAddErrorsResponse errors;
}
