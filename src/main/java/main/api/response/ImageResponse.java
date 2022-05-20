package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageResponse {
    private boolean result;
    private ImageErrorResponse errors;
}
