package main.response;

import lombok.Data;

import java.util.List;

@Data
public class PostsResponse {

    private int count;
    private List<PostResponse> posts;
}
