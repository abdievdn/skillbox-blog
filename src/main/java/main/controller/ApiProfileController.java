package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.profile.ProfileMyRequest;
import main.api.response.profile.ProfileMyResponse;
import main.controller.advice.ErrorsNum;
import main.controller.advice.ErrorsResponseException;
import main.service.*;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/profile/my")
@AllArgsConstructor
public class ApiProfileController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileMyResponse> profileMy(@Valid @RequestBody ProfileMyRequest profileMyRequest,
                                       Principal principal) throws ErrorsResponseException, IOException {
        return ResponseEntity.ok(userService.userProfileChange(profileMyRequest, null, principal));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileMyResponse> profileMyPhoto(MultipartFile photo,
                                            @Valid ProfileMyRequest profileMyRequest,
                                            Principal principal)
            throws ErrorsResponseException, IOException, MaxUploadSizeExceededException {
        ProfileMyResponse profileMyResponse;
        try {
            profileMyResponse = userService.userProfileChange(profileMyRequest, photo, principal);
        } catch (SizeLimitExceededException e) {
            throw new ErrorsResponseException(ErrorsNum.PHOTO);
        }
        return ResponseEntity.ok(profileMyResponse);
    }
}