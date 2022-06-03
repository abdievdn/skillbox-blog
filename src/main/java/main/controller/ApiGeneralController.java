package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.PostCommentAddRequest;
import main.api.request.PostVoteRequest;
import main.api.request.ProfileMyRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
import main.controller.advice.exception.ImageUploadException;
import main.controller.advice.exception.PostCommentAddException;
import main.controller.advice.exception.ProfileMyException;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final ImageService imageService;
    private final PostCommentService postCommentService;
    private final StatisticsService statisticsService;
    private final PostVoteService postVoteService;

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
        return ResponseEntity.ok(settingsResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PutMapping("/settings")
    public ResponseEntity<String> settingsSave(@RequestBody SettingsRequest settingsRequest) {
        settingsService.setGlobalSettings(settingsRequest);
        return ResponseEntity.ok("");
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/statistics/my")
    public ResponseEntity<StatisticsResponse> statisticsMy(Principal principal) {
        StatisticsResponse statisticsResponse = statisticsService.getStatisticsMy(principal);
        if (statisticsResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(statisticsResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> statisticsAll(Principal principal) {
        StatisticsResponse statisticsResponse = statisticsService.getStatisticsAll(principal);
        if (statisticsResponse == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(statisticsResponse);
    }

    @GetMapping("/tag")
    public ResponseEntity<TagsResponse> tag() {
        TagsResponse tagsResponse = tagService.getTags();
        if (tagsResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(tagsResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/image")
    public ResponseEntity<String> image(MultipartFile image) throws ImageUploadException, IOException {
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(imageService.uploadImage(image));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/comment")
    public ResponseEntity<PostCommentAddResponse> comment(@RequestBody PostCommentAddRequest postCommentAddRequest, Principal principal) throws PostCommentAddException {
        PostCommentAddResponse postCommentAddResponse = postCommentService.addComment(postCommentAddRequest, principal);
        if (postCommentAddResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postCommentAddResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/post/like")
    public ResponseEntity<PostVoteResponse> like(@RequestBody PostVoteRequest postVoteRequest, Principal principal) {
        PostVoteResponse postVoteResponse = postVoteService.addPostVote(postVoteRequest, (short) 1, principal);
        if (postVoteResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postVoteResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/post/dislike")
    public ResponseEntity<PostVoteResponse> dislike(@RequestBody PostVoteRequest postVoteRequest, Principal principal) {
        PostVoteResponse postVoteResponse = postVoteService.addPostVote(postVoteRequest, (short) -1, principal);
        if (postVoteResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postVoteResponse);
    }
}
