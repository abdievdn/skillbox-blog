package main.service;

import main.api.request.ProfileMyRequest;
import main.controller.advice.ProfileMyError;
import main.controller.advice.ProfileMyException;
import main.controller.advice.RegisterError;
import main.controller.advice.RegisterException;
import main.api.response.*;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.CaptchaCodeRepository;
import main.model.repository.UserRepository;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    public static final int PHOTO_LIMIT_WEIGHT = 5242880;
    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaRepository;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, CaptchaCodeRepository captchaRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.authenticationManager = authenticationManager;
    }

    private final static String REGEX_NAME = "\\w+\\s?\\w*";

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
        if (userName.isEmpty() || !userName.matches(REGEX_NAME)) {
            throw new RegisterException(RegisterError.NAME);
        }
        String userPassword = registerRequest.getPassword();
        if (userPassword.length() < 6) {
            throw new RegisterException(RegisterError.PASSWORD);
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        user.setPassword(passwordEncoder.encode(userPassword));
        user.setName(userName);
        user.setIsModerator((short) 0);
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

    public ProfileMyResponse userProfileChange(ProfileMyRequest profileMyRequest, MultipartFile photo) throws ProfileMyException {
        ProfileMyResponse profileMyResponse = new ProfileMyResponse();
        String authorizedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!profileMyRequest.getEmail().equals(authorizedUser)) {
            if (userRepository.findByEmail(profileMyRequest.getEmail()).isPresent()) {
                throw new ProfileMyException(ProfileMyError.EMAIL);
            }
        }
        if (profileMyRequest.getName() == null || !profileMyRequest.getName().matches(REGEX_NAME)) {
            throw new ProfileMyException(ProfileMyError.NAME);
        }
        if (profileMyRequest.getPassword() != null && profileMyRequest.getPassword().length() < 6) {
            throw new ProfileMyException(ProfileMyError.PASSWORD);
        }
        User user = userRepository.findByEmail(authorizedUser).orElseThrow();
        if (!profileMyRequest.getEmail().equals(user.getEmail())) {
            user.setEmail(profileMyRequest.getEmail());
        }
        if (!profileMyRequest.getName().equals(user.getName())) {
            user.setName(profileMyRequest.getName());
        }
        if (profileMyRequest.getPassword() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
            user.setPassword(passwordEncoder.encode(profileMyRequest.getPassword()));
        }
        if (profileMyRequest.getRemovePhoto() == 1) {
            user.setPhoto("");
        }
        profileMyResponse.setResult(true);

        if (photo != null) {
            try {
                if (photo.getBytes().length > PHOTO_LIMIT_WEIGHT) {
                    throw new ProfileMyException(ProfileMyError.PHOTO);
                }
                BufferedImage photoInput = ImageIO.read(photo.getInputStream());
                BufferedImage photoOutput = new BufferedImage(90, 90, photoInput.getType());
                Graphics2D avatar = photoOutput.createGraphics();
                avatar.drawImage(photoInput, 0, 0, 90, 90, null);
                avatar.dispose();
                File uploadDir = new File("src\\main\\resources\\static\\avatars\\");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                String filename = photo.getOriginalFilename();
                String formatName = filename.substring(filename.lastIndexOf('.') + 1);
                ImageIO.write(photoOutput, formatName, new File(uploadDir, filename));
                user.setPhoto("/avatars/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userRepository.save(user);
        return profileMyResponse;
    }

    private void createLoginResponse(LoginResponse loginResponse, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());
        userResponse.setEmail(user.getEmail());
        userResponse.setModeration(isModerator(user));
        userResponse.setModerationCount(user.getModerationCount());
        userResponse.setSettings(isModerator(user));
        loginResponse.setResult(true);
        loginResponse.setUser(userResponse);
    }

    private boolean isModerator(User user) {
        return user.getIsModerator() == 1;
    }
}
