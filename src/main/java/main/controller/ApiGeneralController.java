package main.controller;

import lombok.AllArgsConstructor;
import main.api.response.*;
import main.service.CheckService;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final CheckService checkService;
    private final PostService postService;
    private final TagService tagService;

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings() {
        SettingsResponse settingsResponse = settingsService.getGlobalSettings();
        if (settingsResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return  ResponseEntity.ok(settingsResponse);
    }

    @GetMapping("/auth/check")
    public ResponseEntity<CheckResponse> check() {
        CheckResponse checkResponse = checkService.checkUser();
        if (checkResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(checkResponse);
    }

    @GetMapping("/post")
    public ResponseEntity<PostResponse> post() {
        PostResponse postResponse = postService.getPosts();
        if (postResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/tag")
    public ResponseEntity<TagResponse> tag() {
        TagResponse tagResponse = tagService.getTags();
        if (tagResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(tagResponse);
    }
}
