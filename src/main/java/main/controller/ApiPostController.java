package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.post.*;
import main.api.request.post.enums.PostRequestKey;
import main.api.response.post.*;
import main.controller.advice.exception.PostAddEditException;
import main.service.PostService;
import main.service.PostVoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class ApiPostController {

    private final PostService postService;
    private final PostVoteService postVoteService;

    @GetMapping
    public ResponseEntity<?> post(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getActualPosts(postRequest, PostRequestKey.ALL);
        return DefaultController.checkResponse(postsResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> postSearch(PostRequest postRequest) {
        PostsResponse postsResponse = postService.searchPosts(postRequest, PostRequestKey.SEARCH);
        return DefaultController.checkResponse(postsResponse);
    }

    @GetMapping("/byDate")
    public ResponseEntity<?> postByDate(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsByDate(postRequest, PostRequestKey.DATE);
        return DefaultController.checkResponse(postsResponse);
    }

    @GetMapping("/byTag")
    public ResponseEntity<?> postByTag(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsByTag(postRequest, PostRequestKey.TAG);
        return DefaultController.checkResponse(postsResponse);
    }

    @GetMapping("/{ID}")
    public ResponseEntity<?> postById(@PathVariable int ID, Principal principal) {
        PostByIdResponse postByIdResponse = postService.getPostById(ID, principal);
        return DefaultController.checkResponse(postByIdResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/my")
    public ResponseEntity<?> postMy(PostRequest postRequest, Principal principal) {
        PostsResponse postsResponse = postService.getPostsMy(postRequest, principal);
        return DefaultController.checkResponse(postsResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @GetMapping("/moderation")
    public ResponseEntity<?> postModeration(PostRequest postRequest, Principal principal) {
        PostsResponse postsResponse = postService.getPostsModeration(postRequest, principal);
        return DefaultController.checkResponse(postsResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postAdd(@RequestBody PostAddEditRequest postAddRequest,
                                                       Principal principal) throws PostAddEditException {
        PostAddEditResponse postAddResponse = postService.addPost(postAddRequest, principal);
        return DefaultController.checkResponse(postAddResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping(value = "/{ID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postEdit(@RequestBody PostAddEditRequest postAddEditRequest,
                                                        @PathVariable int ID, Principal principal) throws PostAddEditException {
        PostAddEditResponse postAddResponse = postService.editPost(postAddEditRequest, ID, principal);
        if (postAddResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return DefaultController.checkResponse(postAddResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/like")
    public ResponseEntity<?> like(@RequestBody PostVoteRequest postVoteRequest, Principal principal) {
        PostVoteResponse postVoteResponse = postVoteService.addPostVote(postVoteRequest, (short) 1, principal);
        return DefaultController.checkResponse(postVoteResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/dislike")
    public ResponseEntity<?> dislike(@RequestBody PostVoteRequest postVoteRequest, Principal principal) {
        PostVoteResponse postVoteResponse = postVoteService.addPostVote(postVoteRequest, (short) -1, principal);
        return DefaultController.checkResponse(postVoteResponse);
    }
}
