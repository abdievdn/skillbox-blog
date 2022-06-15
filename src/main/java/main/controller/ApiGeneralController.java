package main.controller;

import lombok.AllArgsConstructor;
import main.Blog;
import main.api.request.auth.ProfileMyRequest;
import main.api.request.post.PostCommentAddRequest;
import main.api.request.post.PostModerationRequest;
import main.api.request.general.SettingsRequest;
import main.api.response.BlogResponse;
import main.api.response.auth.ProfileMyResponse;
import main.api.response.general.CalendarResponse;
import main.api.response.general.ImageErrorResponse;
import main.api.response.general.InitResponse;
import main.api.response.post.PostCommentAddResponse;
import main.api.response.post.PostModerationResponse;
import main.controller.advice.error.ProfileMyError;
import main.controller.advice.exception.ImageUploadException;
import main.controller.advice.exception.PostCommentAddException;
import main.controller.advice.exception.ProfileMyException;
import main.service.*;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
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
    private final CalendarService calendarService;
    private final PostService postService;
    private final UserService userService;

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public ResponseEntity<ImageErrorResponse.SettingsResponse> settings() {
        ImageErrorResponse.SettingsResponse settingsResponse = settingsService.getGlobalSettings();
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
    public ResponseEntity<ImageErrorResponse.StatisticsResponse> statisticsMy(Principal principal) {
        ImageErrorResponse.StatisticsResponse statisticsResponse = statisticsService.getStatisticsMy(principal);
        if (statisticsResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(statisticsResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/statistics/all")
    public ResponseEntity<ImageErrorResponse.StatisticsResponse> statisticsAll(Principal principal) {
        ImageErrorResponse.StatisticsResponse statisticsResponse = statisticsService.getStatisticsAll(principal);
        if (statisticsResponse == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(statisticsResponse);
    }

    @GetMapping("/tag")
    public ResponseEntity<InitResponse.TagsResponse> tag() {
        InitResponse.TagsResponse tagsResponse = tagService.getTags();
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

    @GetMapping("/calendar")
    public ResponseEntity<BlogResponse> calendar() {
        CalendarResponse calendarResponse = calendarService.getYears();
        return Blog.checkResponse(calendarResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping(value = "/moderation")
    public ResponseEntity<PostModerationResponse> moderation(@RequestBody PostModerationRequest postModerationRequest, Principal principal) {
        PostModerationResponse postModerationResponse = postService.postModerate(postModerationRequest, principal);
        if (postModerationResponse == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(postModerationResponse);
    }


    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(path = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileMyResponse> profileMy(@RequestBody ProfileMyRequest profileMyRequest,
                                                       Principal principal) throws ProfileMyException, IOException {
        ProfileMyResponse profileMyResponse = userService.userProfileChange(profileMyRequest, null, principal);
        return checkProfileMy(profileMyResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(path = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileMyResponse> profileMyPhoto(MultipartFile photo,
                                                            ProfileMyRequest profileMyRequest,
                                                            Principal principal)
            throws ProfileMyException, IOException {
        ProfileMyResponse profileMyResponse;
        try {
            profileMyResponse = userService.userProfileChange(profileMyRequest, photo, principal);
        }
        catch (SizeLimitExceededException e) {
            throw new ProfileMyException(ProfileMyError.PHOTO);
        }
        return checkProfileMy(profileMyResponse);
    }

    private ResponseEntity<ProfileMyResponse> checkProfileMy(ProfileMyResponse profileMyResponse) {
        if (profileMyResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(profileMyResponse);
    }
}
