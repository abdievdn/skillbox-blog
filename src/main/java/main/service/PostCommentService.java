package main.service;

import main.api.request.PostCommentAddRequest;
import main.api.response.PostCommentAddResponse;
import main.api.response.PostCommentResponse;
import main.api.response.UserResponse;
import main.controller.advice.error.PostCommentAddError;
import main.controller.advice.exception.PostCommentAddException;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import main.service.util.TimestampUtil;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostCommentService(PostCommentRepository postCommentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.postCommentRepository = postCommentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<PostCommentResponse> getComments(int postId) {
        List<PostCommentResponse> commentsToPost = new CopyOnWriteArrayList<>();
        Iterable<PostComment> comments = postCommentRepository.findAll();
        for (PostComment comment : comments) {
            if (comment.getPost().getId() == postId) {
                PostCommentResponse postCommentResponse = new PostCommentResponse();
                postCommentResponse.setId(comment.getId());
                postCommentResponse.setTimestamp(TimestampUtil.encode(comment.getTime()));
                postCommentResponse.setText(comment.getText());
                UserResponse userToComment = new UserResponse();
                userToComment.setId(comment.getUser().getId());
                userToComment.setName(comment.getUser().getName());
                userToComment.setPhoto(comment.getUser().getPhoto());
                postCommentResponse.setUser(userToComment);
                commentsToPost.add(postCommentResponse);
            }
        }
        return commentsToPost;
    }

    public int getCommentsCount(int postId) {
        int commentsCount = 0;
        Iterable<PostComment> postCommentIterable = postCommentRepository.findAll();
        for (PostComment postComment : postCommentIterable) {
            if (postComment.getPost().getId() == postId) commentsCount++;
        }
        return commentsCount;
    }

    public PostCommentAddResponse addComment(PostCommentAddRequest postCommentAddRequest, Principal principal) throws PostCommentAddException {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Post post = postRepository.findById(postCommentAddRequest.getPostId()).orElseThrow();
        if (postCommentAddRequest.getText().length() < 2) throw new PostCommentAddException(PostCommentAddError.TEXT);
        PostComment postComment = new PostComment();
        if (postCommentAddRequest.getParentId() != null) {
            PostComment parentPost = postCommentRepository.findById(postCommentAddRequest.getParentId()).orElseThrow();
            postComment.setParent(parentPost);
        }
        PostCommentAddResponse postCommentAddResponse = new PostCommentAddResponse();
        postComment.setPost(post);
        postComment.setText(postCommentAddRequest.getText());
        postComment.setUser(user);
        postComment.setTime(LocalDateTime.now());
        postCommentRepository.save(postComment);
        postCommentAddResponse.setId(postComment.getId());
        return postCommentAddResponse;
    }
}
