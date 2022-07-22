package main.service;

import lombok.AllArgsConstructor;
import main.api.request.post.PostVoteRequest;
import main.api.response.post.PostVoteResponse;
import main.model.Post;
import main.model.PostVote;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.PostVoteRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostVoteService {

    private final PostVoteRepository postVoteRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public PostVoteResponse addPostVote(PostVoteRequest postVoteRequest, short value, Principal principal) {
        User user = userService.findUser(principal.getName());
        Post post = postRepository.findById(postVoteRequest.getPostId()).orElseThrow();
        PostVoteResponse postVoteResponse = new PostVoteResponse();
        PostVote postVote;
        Optional<PostVote> postVoteIsPresent = postVoteRepository.findByUserAndPost(user, post);
        postVote = postVoteIsPresent.orElseGet(PostVote::new);
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
