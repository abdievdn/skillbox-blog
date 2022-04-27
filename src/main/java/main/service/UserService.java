package main.service;

import main.model.User;
import main.repository.UserRepository;
import main.request.LoginRequest;
import main.response.LoginResponse;
import main.request.RegisterRequest;
import main.response.LogoutResponse;
import main.response.RegisterResponse;
import main.response.UserResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private Map<String, Integer> session;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public RegisterResponse userRegister(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();
        try {
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setName(registerRequest.getName());
            user.setCode(registerRequest.getCaptchaSecret());
            user.setIsModerator(0);
            user.setModerationCount(0);
            user.setPhoto("");
            userRepository.save(user);
            registerResponse.setResult(true);
            registerResponse.setErrors(null);
        } catch (Exception e) {
            registerResponse.setResult(false);
        }
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
        }
        catch (Exception e) {
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

    public LogoutResponse logoutResponse() {
        return logoutResponse();
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
