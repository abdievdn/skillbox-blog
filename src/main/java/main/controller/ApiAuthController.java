package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.LoginRequest;
import main.api.request.PasswordRestoreRequest;
import main.api.request.ProfileMyRequest;
import main.api.response.*;
import main.controller.advice.exception.ProfileMyException;
import main.controller.advice.exception.RegisterException;
import main.api.request.RegisterRequest;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiAuthController {

    private final CaptchaCodeService captchaService;
    private final UserService userService;

    @GetMapping("/auth/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        LoginResponse loginResponse = userService.checkUser(principal);
        if (loginResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/auth/captcha")
    public ResponseEntity<CaptchaCodeResponse> captcha() {
        CaptchaCodeResponse captchaResponse = captchaService.generateCaptcha();
        if (captchaResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(captchaResponse);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest)
            throws RegisterException, FileSizeLimitExceededException {
        RegisterResponse registerResponse = userService.userRegister(registerRequest);
        if (registerResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.userLogin(loginRequest);
        if (loginResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(loginResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/auth/logout")
    public ResponseEntity<LogoutResponse> logout(Principal principal) {
        LogoutResponse logoutResponse = userService.userLogout();
        if (logoutResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(logoutResponse);
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
                                                            Principal principal) throws ProfileMyException, IOException {
        ProfileMyResponse profileMyResponse = userService.userProfileChange(profileMyRequest, photo, principal);
        return checkProfileMy(profileMyResponse);
    }

    private ResponseEntity<ProfileMyResponse> checkProfileMy(ProfileMyResponse profileMyResponse) {
        if (profileMyResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(profileMyResponse);
    }

    @PostMapping("/auth/restore")
    public ResponseEntity<?> passwordRestore(PasswordRestoreRequest passwordRestoreRequest) {
        userService.passwordRestore(passwordRestoreRequest);

        return null;
    }

    @PostMapping("/auth/password")
    public ResponseEntity<?> passwordChange() {
        return null;
    }
}
