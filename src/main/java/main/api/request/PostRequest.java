package main.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {
    private int offset;
    private int limit;
    private String mode;
    private String query;
    private String date;
    private String tag;
}
