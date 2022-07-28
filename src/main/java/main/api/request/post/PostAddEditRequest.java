package main.api.request.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.model.Tag;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAddEditRequest {
    private long timestamp;
    private short active;
    private String title;
    private Set<String> tags;
    private String text;
}
