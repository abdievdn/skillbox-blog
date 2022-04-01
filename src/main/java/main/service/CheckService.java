package main.service;

import main.response.CheckResponse;
import main.model.User;
import main.repository.CheckRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CheckService {

    private static Logger logger = LogManager.getRootLogger();
    private final CheckRepository checkRepository;

    public CheckService(CheckRepository checkRepository) {
        this.checkRepository = checkRepository;
        checkData();
    }

    private void checkData() {
        long usersCount = checkRepository.count();
        if (usersCount < 1) {
            User user = new User(true, 0, new Date(), "admin", "admin@admin.com", "admin");
            checkRepository.save(user);
            logger.warn("Table users was empty and filled with default admin user");
        }
    }

    public CheckResponse checkUser() {
        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setResult(true);
        if (!checkResponse.isResult()) {
            return new CheckResponse();
        }
        checkResponse.setId(576);
        checkResponse.setName("Дмитрий Петров");
        checkResponse.setPhoto("/avatars/ab/cd/ef/52461.jpg");
        checkResponse.setEmail("petrov@petroff.ru");
        checkResponse.setModeration(true);
        checkResponse.setModerationCount(56);
        checkResponse.setSettings(true);
        return checkResponse;
    }
}
