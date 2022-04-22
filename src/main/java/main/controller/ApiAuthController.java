package main.controller;

import lombok.AllArgsConstructor;
import main.response.*;
import main.service.CaptchaCodeService;
import main.service.CheckService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final CheckService checkService;
    private final CaptchaCodeService captchaService;
    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<CheckResponse> check() {
        CheckResponse checkResponse = checkService.checkUser();
        if (checkResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(checkResponse);
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
    public RegisterResultResponse register(RegisterResponse registerResponse) {
        return userService.userRegister(registerResponse);
    }

    @PostMapping("/login")
    public UserResponse login(UserResponse userResponse) {
        return null;
    }

    @GetMapping("/logout")
    public UserResponse logout(UserResponse userResponse) {
        return null;
    }
}
