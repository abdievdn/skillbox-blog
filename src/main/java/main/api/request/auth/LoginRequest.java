package main.api.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull
    @JsonProperty("e_mail")
    private String email;
    @NotNull
    private String password;
}