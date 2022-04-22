package main.service;

import main.model.User;
import main.repository.UserRepository;
import main.response.RegisterResponse;
import main.response.RegisterResultResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResultResponse userRegister(RegisterResponse registerResponse) {
        RegisterResultResponse registerResultResponse = new RegisterResultResponse();
        try {
            User user = new User();
            user.setEmail(registerResponse.getEmail());
            user.setPassword(registerResponse.getPassword());
            user.setName(registerResponse.getName());
            user.setCode(registerResponse.getCaptchaSecret());
            user.setModerator(false);
            user.setModerationCount(0);
            user.setPhoto("");
            userRepository.save(user);
            registerResultResponse.setResult(true);
        }
        catch (Exception e) {
            e.printStackTrace();
            registerResultResponse.setResult(false);
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "Этот e-mail уже зарегистрирован");
            errors.put("name", "Имя указано неверно");
            errors.put("password", "Пароль короче 6-ти символов");
            errors.put("captcha", "Код с картинки введён неверно");
            registerResultResponse.setErrors(errors);
        }
        return registerResultResponse;
    }
}
