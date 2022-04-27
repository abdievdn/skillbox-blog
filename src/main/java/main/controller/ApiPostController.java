package main.controller;

import lombok.AllArgsConstructor;
import main.request.PostRequest;
import main.response.CalendarResponse;
import main.response.PostByIdResponse;
import main.response.PostsResponse;
import main.service.PostService;
import main.request.RequestKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    @GetMapping("/post")
    public ResponseEntity<PostsResponse> post(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getActualPosts(postRequest);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/search")
    public ResponseEntity<PostsResponse> postSearch(PostRequest postRequest) {
        PostsResponse postsResponse = postService.searchPosts(postRequest, RequestKey.SEARCH);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<PostsResponse> postByDate(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsByDate(postRequest, RequestKey.DATE);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<PostsResponse> postByTag(PostRequest postRequest) {
        PostsResponse postsResponse = postService.getPostsByTag(postRequest, RequestKey.TAG);
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/{ID}")
    public ResponseEntity<PostByIdResponse> postById(@PathVariable int ID) {
        PostByIdResponse postByIdResponse = postService.getPostById(ID);
        if (postByIdResponse == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(postByIdResponse);
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
