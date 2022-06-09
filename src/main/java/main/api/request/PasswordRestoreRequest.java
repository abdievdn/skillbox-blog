package main.api.request;

import lombok.Data;

@Data
public class PasswordRestoreRequest {
    private String email;
}
