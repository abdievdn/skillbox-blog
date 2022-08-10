package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.general.PostCommentAddRequest;
import main.api.request.general.PostModerationRequest;
import main.api.request.general.SettingsRequest;
import main.api.response.general.*;
import main.controller.advice.ErrorsResponseException;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiGeneralController {

    private final InitService initService;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final ImageService imageService;
    private final PostCommentService postCommentService;
    private final CalendarService calendarService;
    private final PostService postService;

    @GetMapping("/init")
    public ResponseEntity<InitResponse> init() {
        return ResponseEntity.ok(initService.getInitInfo());
    }

    @GetMapping("/tag")
    public ResponseEntity<TagsResponse> tag() {
        return ResponseEntity.ok(tagService.getTags());
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar() {
        return ResponseEntity.ok(calendarService.getYears());
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings() {
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PutMapping("/settings")
    public ResponseEntity<String> settingsSave(@Valid @RequestBody SettingsRequest settingsRequest) {
        settingsService.setGlobalSettings(settingsRequest);
        return ResponseEntity.ok("");
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/image")
    public ResponseEntity<String> image(@NotNull MultipartFile image)
            throws ErrorsResponseException, IOException, MaxUploadSizeExceededException {
        return ResponseEntity.ok(imageService.uploadImage(image));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/comment")
    public ResponseEntity<PostCommentAddResponse> comment(@Valid @RequestBody PostCommentAddRequest postCommentAddRequest, Principal principal)
            throws ErrorsResponseException {
        PostCommentAddResponse postCommentAddResponse = postCommentService.addComment(postCommentAddRequest, principal);
        return postCommentAddResponse != null ? ResponseEntity.ok(postCommentAddResponse) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping(value = "/moderation")
    public ResponseEntity<PostModerationResponse> moderation(@Valid @RequestBody PostModerationRequest postModerationRequest, Principal principal) {
        return ResponseEntity.ok(postService.postModerate(postModerationRequest, principal));
    }
}