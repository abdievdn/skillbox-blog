package main.response;

import lombok.Data;

import java.util.Map;

@Data
public class PostResponse {

    private int id;
    private long timestamp;
    private Map <String, String> user; // id, name
    private String tittle;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
}
