package service;

import api.response.PostResponse;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class PostService {

    public PostResponse getPosts() {
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(5);
        int count = postResponse.getCount();
        if (count != 0) {
            ArrayList<PostResponse.PostResponseModel> postResponseModels = postResponse.getPosts();
            for (int i = 0; i < count; i++) {
                PostResponse.PostResponseModel postResponseModel = new PostResponse.PostResponseModel();
                postResponseModel.setId(i);
                postResponseModel.setTimestamp((new Timestamp(System.currentTimeMillis() / 1000).getTime()) - (int) ((Math.random() * 100000) + 1));
                PostResponse.PostResponseModel.UserResponseModel userResponseModel = new PostResponse.PostResponseModel.UserResponseModel();
                userResponseModel.setId(i);
                userResponseModel.setName(String.valueOf(UUID.randomUUID()));
                postResponseModel.setUser(userResponseModel);
                postResponseModel.setTittle(String.valueOf(UUID.randomUUID()));
                postResponseModel.setAnnounce(String.valueOf(UUID.randomUUID()));
                postResponseModel.setLikeCount((int) (Math.random() * 100) + 1);
                postResponseModel.setDislikeCount((int) (Math.random() * 100) + 1);
                postResponseModel.setCommentCount((int) (Math.random() * 100) + 1);
                postResponseModel.setViewCount((int) (Math.random() * 100) + 1);
                postResponseModels.add(postResponseModel);
            }
            postResponse.setPosts(postResponseModels);
        }
        return postResponse;
    }
}
