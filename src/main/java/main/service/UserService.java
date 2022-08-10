package main.service;

import lombok.AllArgsConstructor;
import main.Blog;
import main.api.request.auth.*;
import main.api.request.profile.ProfileMyRequest;
import main.api.response.auth.*;
import main.api.response.profile.ProfileMyResponse;
import main.controller.advice.ErrorsNum;
import main.controller.advice.ErrorsResponseException;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import main.service.utils.ImageUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CaptchaCodeService captchaCodeService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    public LoginResponse checkUser(Principal principal) {
        LoginResponse loginResponse = new LoginResponse();
        if (principal != null) {
            createLoginResponse(loginResponse, principal.getName());
        } else {
            loginResponse.setResult(false);
        }
        return loginResponse;
    }

    public RegisterResponse userRegister(RegisterRequest registerRequest) throws ErrorsResponseException {
        RegisterResponse registerResponse = new RegisterResponse();
        if (isEmailPresent(registerRequest.getEmail())) {
            throw new ErrorsResponseException(ErrorsNum.EMAIL);
        }
        if (!captchaCodeService.checkCaptchaCode(registerRequest.getCaptcha(), registerRequest.getCaptchaSecret())) {
            throw new ErrorsResponseException(ErrorsNum.CAPTCHA);
        }
        String userName = registerRequest.getName();
        if (isUserNameIncorrect(userName)) {
            throw new ErrorsResponseException(ErrorsNum.NAME);
        }
        String userPassword = registerRequest.getPassword();
        if (isPasswordIncorrect(userPassword)) {
            throw new ErrorsResponseException(ErrorsNum.PASSWORD);
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userPassword));
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
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getId(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            createLoginResponse(loginResponse, String.valueOf(user.getId()));
        } catch (Exception e) {
            loginResponse.setResult(false);
            loginResponse.setUser(null);
        }
        return loginResponse;
    }

    public ProfileMyResponse userProfileChange(ProfileMyRequest profileMyRequest, MultipartFile photo, Principal principal)
            throws ErrorsResponseException, IOException {
        User user = findUser(principal.getName());
        if (!profileMyRequest.getEmail().equals(user.getEmail()) && isEmailPresent(profileMyRequest.getEmail())) {
            throw new ErrorsResponseException(ErrorsNum.EMAIL);
        } else {
            user.setEmail(profileMyRequest.getEmail());
        }
        if (isUserNameIncorrect(profileMyRequest.getName())) {
            throw new ErrorsResponseException(ErrorsNum.NAME);
        } else {
            user.setName(profileMyRequest.getName());
        }
        if (profileMyRequest.getPassword() != null) {
            if (isPasswordIncorrect(profileMyRequest.getPassword())) {
                throw new ErrorsResponseException(ErrorsNum.PASSWORD);
            } else {
                user.setPassword(passwordEncoder.encode(profileMyRequest.getPassword()));
            }
        }
        if (profileMyRequest.getRemovePhoto() == 1) {
            user.setPhoto("");
        }
        ProfileMyResponse profileMyResponse = new ProfileMyResponse();
        profileMyResponse.setResult(true);
        if (photo != null) {
            if (photo.getBytes().length > Blog.PHOTO_LIMIT_WEIGHT) {
                throw new ErrorsResponseException(ErrorsNum.PHOTO);
            }
            user.setPhoto(savePhoto(photo, principal));
        }
        userRepository.save(user);
        return profileMyResponse;
    }

    public PasswordRestoreResponse passwordRestore(PasswordRestoreRequest passwordRestoreRequest) {
        PasswordRestoreResponse passwordRestoreResponse = new PasswordRestoreResponse();
        try {
            String email = passwordRestoreRequest.getEmail();
            User user = userRepository.findByEmail(email).orElseThrow();
            String code = createRestoreCode();
            user.setCode(code);
            userRepository.save(user);
            mailSenderService.sendEmail(email, createRestoreUrl(code));
            passwordRestoreResponse.setResult(true);
        }
        catch (Exception e) {
            passwordRestoreResponse.setResult(false);
            return passwordRestoreResponse;
        }
        return passwordRestoreResponse;
    }

    public PasswordChangeResponse passwordChange(PasswordChangeRequest passwordChangeRequest) throws ErrorsResponseException {
        if (!captchaCodeService.checkCaptchaCode(passwordChangeRequest.getCaptcha(), passwordChangeRequest.getCaptchaSecret())) {
            throw new ErrorsResponseException(ErrorsNum.CAPTCHA);
        }
        if (isPasswordIncorrect(passwordChangeRequest.getPassword())) {
            throw new ErrorsResponseException(ErrorsNum.PASSWORD);
        }
        User user = userRepository.findByCode(passwordChangeRequest.getCode()).orElseThrow(() -> new ErrorsResponseException(ErrorsNum.CODE));
        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getPassword()));
        user.setCode(null);
        userRepository.save(user);
        PasswordChangeResponse passwordChangeResponse = new PasswordChangeResponse();
        passwordChangeResponse.setResult(true);
        return passwordChangeResponse;
    }

    public User findUser(String userId) {
        return userRepository.findById(Integer.parseInt(userId)).orElseThrow();
    }

    private boolean isEmailPresent(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isUserNameIncorrect(String name) {
        return name.isEmpty() || !name.matches(Blog.REGEX_FOR_USER_NAME);
    }

    private boolean isPasswordIncorrect(String password) {
        return password.length() < Blog.PASSWORD_MIN_LENGTH;
    }

    private boolean isModerator(User user) {
        return user.getIsModerator() == 1;
    }

    private void createLoginResponse(LoginResponse loginResponse, String id) {
        User user = findUser(id);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());
        userResponse.setEmail(user.getEmail());
        userResponse.setModeration(isModerator(user));
        userResponse.setSettings(isModerator(user));
        if (isModerator(user)) {
            userResponse.setModerationCount(getNewPostsCount());
        } else userResponse.setModerationCount(0);
        loginResponse.setResult(true);
        loginResponse.setUser(userResponse);
    }

    private int getNewPostsCount() {
        int postsCount = 0;
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (post.getModerationStatus().equals(ModerationStatus.NEW) && post.getIsActive() == 1) {
                postsCount++;
            }
        }
        return postsCount;
    }

    private String savePhoto(MultipartFile photo, Principal principal) throws IOException {
        String path = Blog.PATH_FOR_AVATARS;
        String fileName = principal.getName();
        String formatName = ImageUtil.getFormatName(photo);
        ImageUtil.saveImage(path, fileName, formatName, photo, Blog.PHOTO_WIDTH, Blog.PHOTO_HEIGHT);
        return path + fileName + '.' + formatName;
    }

    private String createRestoreUrl(String code) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + Blog.EMAIL_URI_FOR_CHANGE_PASSWORD + code;
    }

    private String createRestoreCode() {
        return String.valueOf(UUID.randomUUID()).replace("-", "");
    }
}