package main.api.response.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.controller.advice.ErrorsResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostCommentAddResponse {
    private Integer id;
    private Boolean result;
    private ErrorsResponse errors;
}
