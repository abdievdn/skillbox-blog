package main.api.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PostVoteRequest {
    @NotNull
    @JsonProperty("post_id")
    private int postId;
}