package main.api.request.auth;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PasswordRestoreRequest {
    @NotNull
    private String email;
}