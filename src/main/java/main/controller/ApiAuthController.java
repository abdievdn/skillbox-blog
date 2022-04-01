package main.controller;

import lombok.AllArgsConstructor;
import main.response.CheckResponse;
import main.service.CheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final CheckService checkService;

    @GetMapping("/check")
    public ResponseEntity<CheckResponse> check() {
        CheckResponse checkResponse = checkService.checkUser();
        if (checkResponse == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(checkResponse);
    }
}
