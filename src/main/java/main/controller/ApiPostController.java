package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.post.*;
import main.api.response.post.*;
import main.controller.advice.ErrorsResponseException;
import main.service.PostService;
import main.service.PostVoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class ApiPostController {

    private final PostService postService;
    private final PostVoteService postVoteService;

    @GetMapping({"", "/search", "/byDate", "/byTag"})
    public ResponseEntity<PostsResponse> post(PostRequest postRequest) {
        return checkResponse(postService.getPosts(postRequest, "", false));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/my")
    public ResponseEntity<PostsResponse> postMy(PostRequest postRequest, Principal principal) {
        return checkResponse(postService.getPosts(postRequest, principal.getName(), false));
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @GetMapping("/moderation")
    public ResponseEntity<PostsResponse> postModeration(PostRequest postRequest, Principal principal) {
        return checkResponse(postService.getPosts(postRequest, principal.getName(), true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> postById(@PathVariable int id, Principal principal) {
        PostResponse postResponse = postService.getPostById(id, principal);
        return postResponse != null ? ResponseEntity.ok(postResponse) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostAddEditResponse> postAdd(@Valid @RequestBody PostAddEditRequest postAddRequest,
                                                       Principal principal) throws ErrorsResponseException {
        return ResponseEntity.ok(postService.addPost(postAddRequest, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostAddEditResponse> postEdit(@Valid @RequestBody PostAddEditRequest postAddEditRequest,
                                                        @PathVariable int id, Principal principal) throws ErrorsResponseException {
        return ResponseEntity.ok(postService.editPost(postAddEditRequest, id, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/like")
    public ResponseEntity<?> like(@Valid @RequestBody PostVoteRequest postVoteRequest, Principal principal) {
        return ResponseEntity.ok(postVoteService.addPostVote(postVoteRequest, (short) 1, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/dislike")
    public ResponseEntity<?> dislike(@Valid @RequestBody PostVoteRequest postVoteRequest, Principal principal) {
        return ResponseEntity.ok(postVoteService.addPostVote(postVoteRequest, (short) -1, principal));
    }

    private ResponseEntity<PostsResponse> checkResponse(PostsResponse postsResponse) {
        return postsResponse != null ? ResponseEntity.ok(postsResponse) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}