package main.api.request.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PostModerationRequest {
    @NotNull
    @JsonProperty("post_id")
    private int postId;
    @NotNull
    private String decision;
}