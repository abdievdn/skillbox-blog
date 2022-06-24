package main.api.response.post;

import lombok.Data;
import main.api.response.auth.UserResponse;

@Data
public class PostCommentResponse {
    private int id;
    private long timestamp;
    private String text;
    private UserResponse user; // id, name, photo

}
