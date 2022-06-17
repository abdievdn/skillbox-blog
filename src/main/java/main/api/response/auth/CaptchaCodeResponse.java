package main.api.response.auth;

import lombok.Data;
import main.api.response.BlogResponse;

@Data
public class CaptchaCodeResponse implements BlogResponse {
    private String secret;
    private String image;
}
