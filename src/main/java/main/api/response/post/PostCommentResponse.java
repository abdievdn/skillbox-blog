package main.api.response.post;

import lombok.Data;
import main.api.response.BlogResponse;
import main.api.response.auth.UserResponse;

@Data
public class PostCommentResponse implements BlogResponse {
    private int id;
    private long timestamp;
    private String text;
    private UserResponse user; // id, name, photo

}
