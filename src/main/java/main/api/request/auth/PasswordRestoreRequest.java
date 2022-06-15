package main.api.request.auth;

import lombok.Data;

@Data
public class PasswordRestoreRequest {
    private String email;
}
