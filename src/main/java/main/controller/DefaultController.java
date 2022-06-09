package main.controller;

import lombok.Data;
import main.api.request.PasswordChangeRequest;
import main.api.response.PasswordChangeResponse;
import main.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Data
public class DefaultController {

    private final UserService userService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
