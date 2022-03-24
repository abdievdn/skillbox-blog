package controller;

import api.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.CheckService;
import service.PostService;
import service.SettingsService;
import service.TagService;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final CheckService checkService;
    private final PostService postService;
    private final TagService tagService;

    public ApiGeneralController(InitResponse initResponse,
                                SettingsService settingsService,
                                CheckService checkService,
                                PostService postService,
                                TagService tagService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.checkService = checkService;
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings() {
        SettingsResponse settingsResponse = settingsService.getGlobalSettings();
        if (settingsResponse == null) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
        return  ResponseEntity.ok(settingsResponse);
    }

    @GetMapping("/auth/check")
    public ResponseEntity check() {
        CheckResponse checkResponse = checkService.checkUser();
        if (checkResponse == null) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(checkResponse);
    }

    @GetMapping("/post")
    public ResponseEntity post() {
        PostResponse postResponse = postService.getPosts();
        if (postResponse == null) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/tag")
    public ResponseEntity tag() {
        TagResponse tagResponse = tagService.getTags();
        if (tagResponse == null) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(tagResponse);
    }
}
