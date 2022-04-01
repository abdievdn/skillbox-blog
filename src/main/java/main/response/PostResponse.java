package main.response;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class PostResponse {

    private int count = 0;
    private List<PostsInPostResponse> posts = new CopyOnWriteArrayList<>();

    @Data
    public static class PostsInPostResponse {
        private int id;
        private long timestamp;
        private UserInPostResponse user;
        private String tittle;
        private String announce;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;
        private int viewCount;

        @Data
        public static class UserInPostResponse {
            private int id;
            private String name;
        }
    }
}
