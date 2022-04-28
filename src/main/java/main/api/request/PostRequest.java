package main.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {
    private int offset = 0;
    private int limit = 10;
    private String mode = "recent";
    private String query = null;
    private String date = null;
    private String tag = null;
}
