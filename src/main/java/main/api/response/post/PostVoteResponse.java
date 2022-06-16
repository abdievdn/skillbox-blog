package main.api.response.post;

import lombok.Data;
import main.api.response.BlogResponse;

@Data
public class PostVoteResponse implements BlogResponse {
    private boolean result;
}
