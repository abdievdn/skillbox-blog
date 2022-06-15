package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.auth.*;
import main.api.response.auth.*;
import main.api.response.auth.CaptchaCodeResponse;
import main.controller.advice.exception.RegisterException;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final CaptchaCodeService captchaService;
    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        LoginResponse loginResponse = userService.checkUser(principal);
        if (loginResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaCodeResponse> captcha() {
        CaptchaCodeResponse captchaResponse = captchaService.generateCaptcha();
        if (captchaResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(captchaResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest)
            throws RegisterException, FileSizeLimitExceededException {
        RegisterResponse registerResponse = userService.userRegister(registerRequest);
        if (registerResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.userLogin(loginRequest);
        if (loginResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(loginResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        LogoutResponse logoutResponse = userService.userLogout();
        if (logoutResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(logoutResponse);
    }


    @PostMapping("/restore")
    public ResponseEntity<?> passwordRestore(@RequestBody PasswordRestoreRequest passwordRestoreRequest) {
        PasswordRestoreResponse passwordRestoreResponse = userService.passwordRestore(passwordRestoreRequest);
        if (passwordRestoreResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(passwordRestoreResponse);
    }

    @PostMapping("/password")
    public ResponseEntity<PasswordChangeResponse> passwordChange(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        PasswordChangeResponse passwordChangeResponse = userService.passwordChange(passwordChangeRequest);
        if (passwordChangeResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(passwordChangeResponse);
    }



}
