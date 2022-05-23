package main.api.request;

import lombok.Data;

@Data
public class PostRequest {
    private int offset;
    private int limit;
    private String mode;
    private String query;
    private String date;
    private String tag;
    private String status;
}
