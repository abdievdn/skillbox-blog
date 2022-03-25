package main.api.response;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

@Data
public class PostResponse {

    private int count = 0;
    private ArrayList<PostResponseModel> posts = new ArrayList<>();
    public static final String CONTINUES = "...";

    @Data
    public static class PostResponseModel {
        private int id;
        private long timestamp;
        private UserResponseModel user;
        private String tittle;
        private String announce;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;
        private int viewCount;

        @Data
        public static class UserResponseModel {
            private int id;
            private String name;
        }
    }
}
