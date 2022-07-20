package main.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    private static String DEFAULT_PATH;

    @Value("${blog.path}")
    public void setDefaultPath(String defaultPath) {
        DefaultController.DEFAULT_PATH = defaultPath;
    }

    public static String getDefaultPath() {
        return DEFAULT_PATH;
    }

    @RequestMapping("/**/{path:[^.]*}")
    public String index() {
        return "forward:/";
    }

    public static ResponseEntity<?> checkResponse(Object response) {
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }
}
