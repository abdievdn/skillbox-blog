package main.api.response.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostCommentAddResponse {
    private Integer id;
    private Boolean result;
    private PostCommentAddErrorsResponse errors;
}
