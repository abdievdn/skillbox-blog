package main.api.response.general;

import lombok.Data;
import main.api.response.BlogResponse;

@Data
public class PostModerationResponse implements BlogResponse {
    private boolean result;
}
