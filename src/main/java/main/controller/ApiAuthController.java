package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.auth.*;
import main.api.response.auth.*;
import main.api.response.auth.CaptchaCodeResponse;
import main.controller.advice.ErrorsResponseException;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class ApiAuthController {

    private final CaptchaCodeService captchaService;
    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        return ResponseEntity.ok(userService.checkUser(principal));
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaCodeResponse> captcha() throws IOException {
        return ResponseEntity.ok(captchaService.generateCaptcha());
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest)
            throws ErrorsResponseException {
        return ResponseEntity.ok(userService.userRegister(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.userLogin(loginRequest));
    }

    // logout configured with CustomLogoutSuccessHandler in SecurityConfig

    @PostMapping("/restore")
    public ResponseEntity<PasswordRestoreResponse> passwordRestore(@Valid @RequestBody PasswordRestoreRequest passwordRestoreRequest) {
        return ResponseEntity.ok(userService.passwordRestore(passwordRestoreRequest));
    }

    @PostMapping("/password")
    public ResponseEntity<PasswordChangeResponse> passwordChange(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) throws ErrorsResponseException {
        return ResponseEntity.ok(userService.passwordChange(passwordChangeRequest));
    }
}