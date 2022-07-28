package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.api.response.auth.UserResponse;

import java.util.List;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private int id;
    private long timestamp;
    private UserResponse user; // id, name
    private String title;
    private String announce;
    private String text;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer commentCount;
    private Integer viewCount;
    private List<PostCommentResponse> comments;
    private Set<String> tags;
}
