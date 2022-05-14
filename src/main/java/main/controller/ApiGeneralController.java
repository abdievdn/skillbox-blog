package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.ProfileMyRequest;
import main.api.response.InitResponse;
import main.api.response.ProfileMyResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagsResponse;
import main.controller.advice.ProfileMyException;
import main.service.SettingsService;
import main.service.TagService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final UserService userService;

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

    @GetMapping("/tag")
    public ResponseEntity<TagsResponse> tag() {
        TagsResponse tagsResponse = tagService.getTags();
        if (tagsResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(tagsResponse);
    }

    @PostMapping (path = "/profile/my", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProfileMyResponse> profile(@RequestParam(value = "file", required = false) MultipartFile photo,
                                                     ProfileMyRequest profileMyRequest) throws ProfileMyException {
        ProfileMyResponse profileMyResponse = userService.userProfileChange(profileMyRequest, photo);
        System.out.println(profileMyRequest);
        if (profileMyResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(profileMyResponse);
    }
}
