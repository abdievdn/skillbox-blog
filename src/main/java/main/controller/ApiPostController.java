package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.PostRequest;
import main.api.response.CalendarResponse;
import main.api.response.PostByIdResponse;
import main.api.response.PostsResponse;
import main.service.PostService;
import main.api.request.PostRequestKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        if (postByIdResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postByIdResponse);
    }

    @GetMapping("/post/my")
    public ResponseEntity<PostsResponse> postMy(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsMy(postRequest);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar() {
        CalendarResponse calendarResponse = postService.getYears();
        if (calendarResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(calendarResponse);
    }

    private ResponseEntity<PostsResponse> checkPostResponseEntity(PostsResponse postsResponse) {
        if (postsResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postsResponse);
    }
}
