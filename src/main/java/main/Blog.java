package main;

import main.api.response.BlogResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Blog {

    public static ResponseEntity<BlogResponse> checkResponse(BlogResponse blogResponse) {
        if (blogResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(blogResponse);
    }
}
