package main.api.request.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PostCommentAddRequest {
    @JsonProperty("parent_id")
    private Integer parentId;
    @NotNull
    @JsonProperty("post_id")
    private int postId;
    @NotNull
    private String text;
}