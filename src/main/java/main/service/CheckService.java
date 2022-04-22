package main.service;

import main.repository.UserRepository;
import main.response.CheckResponse;
import main.model.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CheckService {

    private final UserRepository userRepository;

    public CheckService(UserRepository checkRepository) {
        this.userRepository = checkRepository;
        checkData();
    }

    private void checkData() {
        long usersCount = userRepository.count();
        if (usersCount < 1) {
            User user = new User(true, 0, new Date(), "admin", "admin@admin.com", "admin");
            userRepository.save(user);
        }
    }

    public CheckResponse checkUser() {
        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setResult(false);
        return checkResponse;
    }
}
