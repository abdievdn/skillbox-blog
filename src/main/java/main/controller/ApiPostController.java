package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.PostModerationRequest;
import main.api.request.PostAddEditRequest;
import main.api.request.PostRequest;
import main.api.response.*;
import main.controller.advice.exception.PostAddEditException;
import main.service.PostService;
import main.api.request.PostRequestKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    @GetMapping("/post")
    public ResponseEntity<PostsResponse> post(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getActualPosts(postRequest, PostRequestKey.ALL);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/search")
    public ResponseEntity<PostsResponse> postSearch(PostRequest postRequest) {
        PostsResponse postsResponse = postService.searchPosts(postRequest, PostRequestKey.SEARCH);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<PostsResponse> postByDate(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsByDate(postRequest, PostRequestKey.DATE);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<PostsResponse> postByTag(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsByTag(postRequest, PostRequestKey.TAG);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/{ID}")
    public ResponseEntity<PostByIdResponse> postById(@PathVariable int ID) {
        PostByIdResponse postByIdResponse = postService.getPostById(ID);
        if (postByIdResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postByIdResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/post/my")
    public ResponseEntity<PostsResponse> postMy(PostRequest postRequest, Principal principal) {
        PostsResponse postsResponse = postService.getPostsMy(postRequest, principal);
        return checkPostResponseEntity(postsResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @GetMapping("/post/moderation")
    public ResponseEntity<PostsResponse> postModeration(PostRequest postRequest, Principal principal) {
        PostsResponse postsResponse = postService.getPostsModeration(postRequest, principal);
        return checkPostResponseEntity(postsResponse);
    }

    private ResponseEntity<PostsResponse> checkPostResponseEntity(PostsResponse postsResponse) {
        if (postsResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postsResponse);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar() {
        CalendarResponse calendarResponse = postService.getYears();
        if (calendarResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(calendarResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostAddEditResponse> postAdd(@RequestBody PostAddEditRequest postAddRequest,
                                                       Principal principal) throws PostAddEditException {
        PostAddEditResponse postAddResponse = postService.addPost(postAddRequest, principal);
        if (postAddResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postAddResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping(value = "/post/{ID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostAddEditResponse> postEdit(@RequestBody PostAddEditRequest postAddEditRequest,
                                                        @PathVariable int ID) throws PostAddEditException {
        PostAddEditResponse postAddResponse = postService.editPost(postAddEditRequest, ID);
        if (postAddResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postAddResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping(value = "/moderation")
    public ResponseEntity<PostModerationResponse> moderation(@RequestBody PostModerationRequest postModerationRequest, Principal principal) {
        PostModerationResponse postModerationResponse = postService.postModerate(postModerationRequest, principal);
        if (postModerationResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postModerationResponse);
    }


}
