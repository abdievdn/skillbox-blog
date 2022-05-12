package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.LoginRequest;
import main.api.request.ProfileMyRequest;
import main.api.response.*;
import main.controller.advice.RegisterException;
import main.api.request.RegisterRequest;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) throws RegisterException {
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

    @GetMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        LogoutResponse logoutResponse = userService.userLogout();
        if (logoutResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(logoutResponse);
    }


}
