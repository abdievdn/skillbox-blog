package main.service;

import lombok.AllArgsConstructor;
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

    public RegisterResponse userRegister(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();
        RegisterErrorsResponse registerErrorsResponse = new RegisterErrorsResponse();
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            registerErrorsResponse.setEmail("Этот e-mail уже зарегистрирован");
        } else {
            Iterable<CaptchaCode> captchaCodeIterable = captchaRepository.findAll();
            registerErrorsResponse.setCaptcha("Код с картинки введён неверно");
            for (CaptchaCode captcha : captchaCodeIterable) {
                if (captcha.getCode().equals(registerRequest.getCaptcha()) &&
                        captcha.getSecretCode().equals(registerRequest.getCaptchaSecret())) {
                    registerErrorsResponse.setCaptcha(null);
                    String name = registerRequest.getName();
                    String password = registerRequest.getPassword();
                    if (name.isEmpty() || !name.matches("\\w*\\s?\\w*")) {
                        registerErrorsResponse.setName("Имя указано неверно");
                        break;
                    }
                    if (password.length() < 6) {
                        registerErrorsResponse.setPassword("Пароль короче 6-ти символов");
                        break;
                    }
                    User user = new User();
                    user.setEmail(registerRequest.getEmail());
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                    user.setName(name);
                    user.setIsModerator(0);
                    user.setModerationCount(0);
                    user.setPhoto("");
                    user.setRegTime(LocalDateTime.now());
                    userRepository.save(user);
                    registerResponse.setResult(true);
                    registerErrorsResponse = null;
                    break;
                }
            }
        }
        registerResponse.setErrors(registerErrorsResponse);
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
