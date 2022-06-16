package main.api.response.post;

import lombok.Data;
import main.api.response.BlogResponse;

import java.util.List;

@Data
public class PostsResponse implements BlogResponse {
    private int count;
    private List<PostResponse> posts;
}
