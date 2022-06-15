package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.post.*;
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

@AllArgsConstructor
@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;
    private final PostVoteService postVoteService;

    @GetMapping
    public ResponseEntity<PostsResponse> post(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getActualPosts(postRequest, PostRequestKey.ALL);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<PostsResponse> postSearch(PostRequest postRequest) {
        PostsResponse postsResponse = postService.searchPosts(postRequest, PostRequestKey.SEARCH);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostsResponse> postByDate(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsByDate(postRequest, PostRequestKey.DATE);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostsResponse> postByTag(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsByTag(postRequest, PostRequestKey.TAG);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/{ID}")
    public ResponseEntity<PostByIdResponse> postById(@PathVariable int ID) {
        PostByIdResponse postByIdResponse = postService.getPostById(ID);
        if (postByIdResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postByIdResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/my")
    public ResponseEntity<PostsResponse> postMy(PostRequest postRequest, Principal principal) {
        PostsResponse postsResponse = postService.getPostsMy(postRequest, principal);
        return checkPostResponseEntity(postsResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @GetMapping("/moderation")
    public ResponseEntity<PostsResponse> postModeration(PostRequest postRequest, Principal principal) {
        PostsResponse postsResponse = postService.getPostsModeration(postRequest, principal);
        return checkPostResponseEntity(postsResponse);
    }

    private ResponseEntity<PostsResponse> checkPostResponseEntity(PostsResponse postsResponse) {
        if (postsResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postsResponse);
    }



    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostAddEditResponse> postAdd(@RequestBody PostAddEditRequest postAddRequest,
                                                       Principal principal) throws PostAddEditException {
        PostAddEditResponse postAddResponse = postService.addPost(postAddRequest, principal);
        if (postAddResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postAddResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping(value = "/{ID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostAddEditResponse> postEdit(@RequestBody PostAddEditRequest postAddEditRequest,
                                                        @PathVariable int ID) throws PostAddEditException {
        PostAddEditResponse postAddResponse = postService.editPost(postAddEditRequest, ID);
        if (postAddResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postAddResponse);
    }



    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/like")
    public ResponseEntity<PostVoteResponse> like(@RequestBody PostVoteRequest postVoteRequest, Principal principal) {
        PostVoteResponse postVoteResponse = postVoteService.addPostVote(postVoteRequest, (short) 1, principal);
        if (postVoteResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postVoteResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/dislike")
    public ResponseEntity<PostVoteResponse> dislike(@RequestBody PostVoteRequest postVoteRequest, Principal principal) {
        PostVoteResponse postVoteResponse = postVoteService.addPostVote(postVoteRequest, (short) -1, principal);
        if (postVoteResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postVoteResponse);
    }

}
