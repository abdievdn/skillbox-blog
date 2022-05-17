package main.api.response;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PostByIdResponse {

    private int id;
    private long timestamp;
    private UserResponse user; // id, name
    private String tittle;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<PostCommentResponse> comments;
    private Set<String> tags;

}
