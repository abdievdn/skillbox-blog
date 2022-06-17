package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.general.ProfileMyRequest;
import main.api.request.general.PostCommentAddRequest;
import main.api.request.general.PostModerationRequest;
import main.api.request.general.SettingsRequest;
import main.api.response.BlogResponse;
import main.api.response.general.*;
import main.api.response.general.PostCommentAddResponse;
import main.api.response.general.PostModerationResponse;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
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
    public ResponseEntity<BlogResponse> settings() {
        SettingsResponse settingsResponse = settingsService.getGlobalSettings();
        return DefaultController.checkResponse(settingsResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PutMapping("/settings")
    public ResponseEntity<String> settingsSave(@RequestBody SettingsRequest settingsRequest) {
        settingsService.setGlobalSettings(settingsRequest);
        return ResponseEntity.ok("");
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/statistics/my")
    public ResponseEntity<BlogResponse> statisticsMy(Principal principal) {
        StatisticsResponse statisticsResponse = statisticsService.getStatisticsMy(principal);
        return DefaultController.checkResponse(statisticsResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/statistics/all")
    public ResponseEntity<BlogResponse> statisticsAll(Principal principal) {
        StatisticsResponse statisticsResponse = statisticsService.getStatisticsAll(principal);
        return DefaultController.checkResponse(statisticsResponse);
    }

    @GetMapping("/tag")
    public ResponseEntity<BlogResponse> tag() {
        TagsResponse tagsResponse = tagService.getTags();
        return DefaultController.checkResponse(tagsResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/image")
    public ResponseEntity<String> image(MultipartFile image)
            throws ImageUploadException, IOException, MaxUploadSizeExceededException {
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(imageService.uploadImage(image));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/comment")
    public ResponseEntity<BlogResponse> comment(@RequestBody PostCommentAddRequest postCommentAddRequest, Principal principal) throws PostCommentAddException {
        PostCommentAddResponse postCommentAddResponse = postCommentService.addComment(postCommentAddRequest, principal);
        return DefaultController.checkResponse(postCommentAddResponse);
    }

    @GetMapping("/calendar")
    public ResponseEntity<BlogResponse> calendar() {
        CalendarResponse calendarResponse = calendarService.getYears();
        return DefaultController.checkResponse(calendarResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping(value = "/moderation")
    public ResponseEntity<BlogResponse> moderation(@RequestBody PostModerationRequest postModerationRequest, Principal principal) {
        PostModerationResponse postModerationResponse = postService.postModerate(postModerationRequest, principal);
        return DefaultController.checkResponse(postModerationResponse);
    }


    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(path = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BlogResponse> profileMy(@RequestBody ProfileMyRequest profileMyRequest,
                                                       Principal principal) throws ProfileMyException, IOException {
        ProfileMyResponse profileMyResponse = userService.userProfileChange(profileMyRequest, null, principal);
        return DefaultController.checkResponse(profileMyResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(path = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogResponse> profileMyPhoto(MultipartFile photo,
                                                            ProfileMyRequest profileMyRequest,
                                                            Principal principal)
            throws ProfileMyException, IOException, MaxUploadSizeExceededException {
        ProfileMyResponse profileMyResponse;
        try {
            profileMyResponse = userService.userProfileChange(profileMyRequest, photo, principal);
        }
        catch (SizeLimitExceededException e) {
            throw new ProfileMyException(ProfileMyError.PHOTO);
        }
        return DefaultController.checkResponse(profileMyResponse);
    }
}