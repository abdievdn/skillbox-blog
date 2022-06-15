package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAddEditErrorResponse {

    private String title;
    private String text;
}
