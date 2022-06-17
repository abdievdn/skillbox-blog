package main.api.request.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAddEditRequest {
    private long timestamp;
    private short active;
    private String title;
    private String[] tags;
    private String text;
}
