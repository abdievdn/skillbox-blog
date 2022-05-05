package main.service;

import lombok.AllArgsConstructor;
import main.controller.advice.RegisterError;
import main.controller.advice.RegisterException;
import main.api.response.*;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.CaptchaCodeRepository;
import main.model.repository.UserRepository;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaRepository;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse userRegister(RegisterRequest registerRequest) throws RegisterException {
        RegisterResponse registerResponse = new RegisterResponse();
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RegisterException(RegisterError.EMAIL);
        }
        Optional<CaptchaCode> captchaCode = captchaRepository.findByCode(registerRequest.getCaptcha());
        if (captchaCode.isPresent()) {
            if (!captchaCode.get().getSecretCode().equals(registerRequest.getCaptchaSecret())) {
                return null;
            }
        } else {
            throw new RegisterException(RegisterError.CAPTCHA);
        }
        String userName = registerRequest.getName();
        if (userName.isEmpty() || !userName.matches("\\w+\\s?\\w*")) {
            throw new RegisterException(RegisterError.NAME);
        }
        String userPassword = registerRequest.getPassword();
        if (userPassword.length() < 6) {
            throw new RegisterException(RegisterError.PASSWORD);
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(userPassword));
        user.setName(userName);
        user.setIsModerator(0);
        user.setModerationCount(0);
        user.setPhoto("");
        user.setRegTime(LocalDateTime.now());
        userRepository.save(user);
        registerResponse.setResult(true);
        return registerResponse;
    }

    public LoginResponse userLogin(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            org.springframework.security.core.userdetails.User userLogin =
                    (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            createLoginResponse(loginResponse, userLogin.getUsername());
        } catch (Exception e) {
            loginResponse.setResult(false);
            loginResponse.setUser(null);
        }
        return loginResponse;
    }

    public LoginResponse checkUser(Principal principal) {
        LoginResponse loginResponse = new LoginResponse();
        if (principal != null) {
            createLoginResponse(loginResponse, principal.getName());
        } else {
            loginResponse.setResult(false);
        }
        return loginResponse;
    }

    public LogoutResponse userLogout() {
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setResult(true);
        SecurityContextHolder.clearContext();
        return logoutResponse;
    }

    private void createLoginResponse(LoginResponse loginResponse, String email) {
        Optional<User> user = userRepository.findByEmail(email);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.get().getId());
        userResponse.setName(user.get().getName());
        userResponse.setPhoto(user.get().getPhoto());
        userResponse.setEmail(user.get().getEmail());
        userResponse.setModeration(isModerator(user.get()));
        userResponse.setModerationCount(user.get().getModerationCount());
        userResponse.setSettings(isModerator(user.get()));
        loginResponse.setResult(true);
        loginResponse.setUser(userResponse);
    }

    private boolean isModerator(User user) {
        return user.getIsModerator() == 1;
    }
}
