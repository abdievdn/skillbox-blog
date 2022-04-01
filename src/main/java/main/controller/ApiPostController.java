package main.controller;

import lombok.AllArgsConstructor;
import main.response.CalendarResponse;
import main.response.PostResponse;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    @GetMapping("/post")
    public ResponseEntity<PostResponse> post(@RequestParam Map<String, String> params) {
        PostResponse postResponse =
                postService.getActualPosts(params.get("offset"), params.get("limit"), params.get("mode"));
        if (postResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/post/search")
    public ResponseEntity<PostResponse> postSearch(@RequestParam Map<String, String> params) {
        PostResponse postResponse =
                postService.searchPosts(params.get("offset"), params.get("limit"), params.get("query"));
        if (postResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<PostResponse> postByDate(@RequestParam Map<String, String> params) {
        return null;
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<PostResponse> postByTag(@RequestParam Map<String, String> params) {
        return null;
    }

    @GetMapping("/post/{ID}")
    public ResponseEntity<PostResponse> postById(@PathVariable int ID) {
        return null;
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar() {
        CalendarResponse calendarResponse = postService.getYears();
        if (calendarResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        System.out.println(calendarResponse.toString());
        return ResponseEntity.ok(calendarResponse);
    }
}
