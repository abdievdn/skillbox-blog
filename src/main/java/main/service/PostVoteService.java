package main.service;

import main.api.request.PostVoteRequest;
import main.api.response.PostVoteResponse;
import main.model.Post;
import main.model.PostVote;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.PostVoteRepository;
import main.model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostVoteService {

    private final PostVoteRepository postVoteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostVoteService(PostVoteRepository postVoteRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postVoteRepository = postVoteRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public PostVoteResponse addPostVote(PostVoteRequest postVoteRequest, short value, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Post post = postRepository.findById(postVoteRequest.getPostId()).orElseThrow();
        PostVoteResponse postVoteResponse = new PostVoteResponse();
        PostVote postVote;
        Optional<PostVote> postVoteIsPresent = postVoteRepository.findByUserAndPost(user, post);
        if (postVoteIsPresent.isPresent()) {
            postVote = postVoteIsPresent.get();
        } else {
            postVote = new PostVote();
        }
        postVote.setUser(user);
        postVote.setPost(post);
        postVote.setTime(LocalDateTime.now());
        if (postVote.getValue() == null || postVote.getValue() != value) {
            postVote.setValue(value);
            postVoteResponse.setResult(true);
        } else if (postVote.getValue() == value) {
            postVote.setValue((short) 0);
            postVoteResponse.setResult(false);
        }
        postVoteRepository.save(postVote);
        return postVoteResponse;
    }

    public int getPostVoteCount(int postId, short arg) {
        int postVoteCount = 0;
        Iterable<PostVote> postVoteIterable = postVoteRepository.findAll();
        for (PostVote postVote : postVoteIterable) {
            if (postVote.getPost().getId() == postId && postVote.getValue() == arg) {
                postVoteCount++;
            }
        }
        return postVoteCount;
    }
}
