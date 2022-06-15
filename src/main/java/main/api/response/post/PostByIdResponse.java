package main.api.response.post;

import lombok.Data;
import main.api.response.auth.UserResponse;

import java.util.List;
import java.util.Set;

@Data
public class PostByIdResponse {

    private int id;
    private long timestamp;
    private UserResponse user; // id, name
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<PostCommentResponse> comments;
    private Set<String> tags;

}
