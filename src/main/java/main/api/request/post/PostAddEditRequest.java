package main.api.request.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.model.Tag;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAddEditRequest {
    @NotNull
    private long timestamp;
    @NotNull
    private short active;
    @NotNull
    private String title;
    private Set<String> tags;
    @NotNull
    private String text;
}
