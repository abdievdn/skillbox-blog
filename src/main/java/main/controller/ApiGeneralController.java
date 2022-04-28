package main.controller;

import lombok.AllArgsConstructor;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagsResponse;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
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

    @GetMapping("/tag")
    public ResponseEntity<TagsResponse> tag() {
        TagsResponse tagsResponse = tagService.getTags();
        if (tagsResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(tagsResponse);
    }
}
