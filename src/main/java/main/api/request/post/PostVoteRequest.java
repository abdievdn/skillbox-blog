package main.api.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostVoteRequest {
    @JsonProperty("post_id")
    private int postId;
}
