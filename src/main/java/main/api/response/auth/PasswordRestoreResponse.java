package main.api.response.auth;

import lombok.Data;
import main.api.response.BlogResponse;

@Data
public class PasswordRestoreResponse implements BlogResponse {
    private boolean result;
}
