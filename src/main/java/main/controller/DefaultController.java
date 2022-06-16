package main.controller;

import main.api.response.BlogResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultController {

//    @RequestMapping("/")
//    public String index() {
//        return "index";
//    }

    @Value("${blog.path}")
    private String defaultPath;
    private static String DEFAULT_PATH;

    @Value("${blog.path}")
    public void setDefaultPath(String defaultPath) {
        DefaultController.DEFAULT_PATH = defaultPath;
    }

    public static String getDefaultPath() {
        return DEFAULT_PATH;
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{path:[^\\.]*}")
    public String redirectToIndex() {
        return "forward:/";
    }

    public static ResponseEntity<BlogResponse> checkResponse(BlogResponse blogResponse) {
        if (blogResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(blogResponse);
    }
}
