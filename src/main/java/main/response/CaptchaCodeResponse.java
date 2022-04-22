package main.response;

import lombok.Data;

@Data
public class CaptchaCodeResponse {
    private String secret;
    private String image;
}
