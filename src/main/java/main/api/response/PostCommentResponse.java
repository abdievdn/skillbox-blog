package main.api.response;

import lombok.Data;

@Data
public class PostCommentResponse {

    private int id;
    private long timestamp;
    private String text;
    private UserResponse user; // id, name, photo

}
