package service;

import api.response.CheckResponse;
import model.User;
import org.springframework.stereotype.Service;

@Service
public class CheckService {

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
