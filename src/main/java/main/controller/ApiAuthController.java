package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.auth.*;
import main.api.response.BlogResponse;
import main.api.response.auth.*;
import main.api.response.auth.CaptchaCodeResponse;
import main.controller.advice.exception.PasswordChangeException;
import main.controller.advice.exception.RegisterException;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class ApiAuthController {

    private final CaptchaCodeService captchaService;
    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<BlogResponse> check(Principal principal) {
        LoginResponse loginResponse = userService.checkUser(principal);
        return DefaultController.checkResponse(loginResponse);
    }

    @GetMapping("/captcha")
    public ResponseEntity<BlogResponse> captcha() {
        CaptchaCodeResponse captchaResponse = captchaService.generateCaptcha();
        return DefaultController.checkResponse(captchaResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<BlogResponse> register(@RequestBody RegisterRequest registerRequest)
            throws RegisterException, FileSizeLimitExceededException {
        RegisterResponse registerResponse = userService.userRegister(registerRequest);
        return DefaultController.checkResponse(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<BlogResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.userLogin(loginRequest);
        return DefaultController.checkResponse(loginResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/logout")
    public ResponseEntity<BlogResponse> logout() {
        LogoutResponse logoutResponse = userService.userLogout();
        return DefaultController.checkResponse(logoutResponse);
    }

    @PostMapping("/restore")
    public ResponseEntity<BlogResponse> passwordRestore(@RequestBody PasswordRestoreRequest passwordRestoreRequest) {
        PasswordRestoreResponse passwordRestoreResponse = userService.passwordRestore(passwordRestoreRequest);
        return DefaultController.checkResponse(passwordRestoreResponse);
    }

    @PostMapping("/password")
    public ResponseEntity<BlogResponse> passwordChange(@RequestBody PasswordChangeRequest passwordChangeRequest) throws PasswordChangeException {
        PasswordChangeResponse passwordChangeResponse = userService.passwordChange(passwordChangeRequest);
        return DefaultController.checkResponse(passwordChangeResponse);
    }
}
