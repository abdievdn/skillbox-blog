package main.service;

import main.Blog;
import main.api.request.auth.*;
import main.api.request.general.ProfileMyRequest;
import main.api.response.auth.*;
import main.api.response.general.ProfileMyResponse;
import main.controller.advice.error.PasswordChangeError;
import main.controller.advice.error.ProfileMyError;
import main.controller.advice.exception.PasswordChangeException;
import main.controller.advice.exception.ProfileMyException;
import main.controller.advice.error.RegisterError;
import main.controller.advice.exception.RegisterException;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.CaptchaCodeRepository;
import main.model.repository.UserRepository;
import main.service.utils.ImageUtil;
import main.service.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaRepository;
    private final AuthenticationManager authenticationManager;
    private final PostService postService;
    private final MailSender mailSender;

    @Value("${blog.info.email}")
    private String emailFrom;

    public UserService(UserRepository userRepository, CaptchaCodeRepository captchaRepository, AuthenticationManager authenticationManager, PostService postService, MailSender mailSender) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.authenticationManager = authenticationManager;
        this.postService = postService;
        this.mailSender = mailSender;
    }

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
        if (userName.isEmpty() || !userName.matches(Blog.REGEX_FOR_USER_NAME)) {
            throw new RegisterException(RegisterError.NAME);
        }
        String userPassword = registerRequest.getPassword();
        if (userPassword.length() < 6) {
            throw new RegisterException(RegisterError.PASSWORD);
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(PasswordUtil.encodePassword(userPassword));
        user.setName(userName);
        user.setIsModerator((short) 0);
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

    public ProfileMyResponse userProfileChange(ProfileMyRequest profileMyRequest, MultipartFile photo, Principal principal)
            throws ProfileMyException, IOException {
        ProfileMyResponse profileMyResponse = new ProfileMyResponse();
        String authorizedUser = principal.getName();
        if (!profileMyRequest.getEmail().equals(authorizedUser)) {
            if (userRepository.findByEmail(profileMyRequest.getEmail()).isPresent()) {
                throw new ProfileMyException(ProfileMyError.EMAIL);
            }
        }
        if (profileMyRequest.getName() == null || !profileMyRequest.getName().matches(Blog.REGEX_FOR_USER_NAME)) {
            throw new ProfileMyException(ProfileMyError.NAME);
        }
        if (profileMyRequest.getPassword() != null && profileMyRequest.getPassword().length() < Blog.PASSWORD_MIN_LENGTH) {
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
            user.setPassword(PasswordUtil.encodePassword(profileMyRequest.getPassword()));
        }
        if (profileMyRequest.getRemovePhoto() == 1) {
            user.setPhoto("");
        }
        profileMyResponse.setResult(true);

        if (photo != null) {
            if (photo.getBytes().length > Blog.PHOTO_LIMIT_WEIGHT) {
                throw new ProfileMyException(ProfileMyError.PHOTO);
            }
            String path = Blog.PATH_FOR_AVATARS;
            String fileName = String.valueOf(user.getId());
            String formatName = ImageUtil.getFormatName(photo);
            ImageUtil.saveImage(path, fileName, formatName, photo, Blog.PHOTO_WIDTH, Blog.PHOTO_HEIGHT);
            user.setPhoto(path + fileName + '.' + formatName);
        }
        userRepository.save(user);
        return profileMyResponse;
    }

    public PasswordRestoreResponse passwordRestore(PasswordRestoreRequest passwordRestoreRequest) {
        String email = passwordRestoreRequest.getEmail();
        PasswordRestoreResponse passwordRestoreResponse = new PasswordRestoreResponse();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            passwordRestoreResponse.setResult(false);
            return passwordRestoreResponse;
        }
        String code = String.valueOf(UUID.randomUUID()).replace("-", "");
        user.get().setCode(code);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String restoreUrl = baseUrl + Blog.EMAIL_URI_FOR_CHANGE_PASSWORD + code;
        sendEmail(email, restoreUrl);
        passwordRestoreResponse.setResult(true);
        userRepository.save(user.get());
        return passwordRestoreResponse;
    }

    private void sendEmail(String email, String text) {
        SimpleMailMessage simpleMail = new SimpleMailMessage();
        simpleMail.setFrom(emailFrom);
        simpleMail.setTo(email);
        simpleMail.setSubject(Blog.EMAIL_SUBJECT);
        simpleMail.setText(text);
        mailSender.send(simpleMail);
    }

    public PasswordChangeResponse passwordChange(PasswordChangeRequest passwordChangeRequest) throws PasswordChangeException {
        PasswordChangeResponse passwordChangeResponse = new PasswordChangeResponse();
        String code = passwordChangeRequest.getCode();
        Optional<CaptchaCode> captchaCode = captchaRepository.findByCode(passwordChangeRequest.getCaptcha());
        if (captchaCode.isPresent()) {
            if (!captchaCode.get().getSecretCode().equals(passwordChangeRequest.getCaptchaSecret())) {
                return null;
            }
        } else {
            throw new PasswordChangeException(PasswordChangeError.CAPTCHA);
        }
        if (passwordChangeRequest.getPassword().length() < 6) throw new PasswordChangeException(PasswordChangeError.PASSWORD);
        Optional<User> user = userRepository.findByCode(code);
        if (user.isEmpty()) throw new PasswordChangeException(PasswordChangeError.CODE);
        user.get().setPassword(PasswordUtil.encodePassword(passwordChangeRequest.getPassword()));
        user.get().setCode(null);
        userRepository.save(user.get());
        passwordChangeResponse.setResult(true);
        return passwordChangeResponse;
    }

    private void createLoginResponse(LoginResponse loginResponse, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());
        userResponse.setEmail(user.getEmail());
        userResponse.setModeration(isModerator(user));
        if (user.getIsModerator() == 1) {
            userResponse.setModerationCount(postService.getNewPostsCount());
        } else userResponse.setModerationCount(0);
        userResponse.setSettings(isModerator(user));
        loginResponse.setResult(true);
        loginResponse.setUser(userResponse);
    }

    private boolean isModerator(User user) {
        return user.getIsModerator() == 1;
    }
}
