package main.response;

import lombok.Data;

import java.util.Map;

@Data
public class PostCommentResponse {

    private int id;
    private long timestamp;
    private String text;
    private UserResponse user; // id, name, photo

}
