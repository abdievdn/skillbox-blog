package main.api.response.general;

import lombok.Data;
import main.api.response.BlogResponse;

@Data
public class InitResponse implements BlogResponse {
    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;
}
