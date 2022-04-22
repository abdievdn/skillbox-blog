package main.controller;

import main.response.CalendarResponse;
import main.response.PostByIdResponse;
import main.response.PostsResponse;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    public static final String
            OFFSET = "offset",
            LIMIT = "limit",
            MODE = "mode";
    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post")
    public ResponseEntity<PostsResponse> post(@RequestParam Map<String, String> params) {
        PostsResponse postsResponse = postService
                .getActualPosts(params.get(OFFSET), params.get(LIMIT), params.get(MODE));
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/search")
    public ResponseEntity<PostsResponse> postSearch(@RequestParam Map<String, String> params) {
        PostsResponse postsResponse = postService
                .searchPosts(params.get(OFFSET), params.get(LIMIT), params.get("query"), "query");
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<PostsResponse> postByDate(@RequestParam Map<String, String> params) {
        PostsResponse postsResponse = postService
                .getPostsByDate(params.get(OFFSET), params.get(LIMIT), params.get("date"), "date");
        return checkPostResponseEntity(postsResponse);
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<PostsResponse> postByTag(@RequestParam Map<String, String> params) {
        PostsResponse postsResponse = postService
                .getPostsByTag(params.get(OFFSET), params.get(LIMIT), params.get("tag"), "tag");
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
