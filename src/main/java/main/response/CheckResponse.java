package main.response;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckResponse {

    private boolean result;

    private Integer id;

    private String name;

    private String photo;

    private String email;

    private Boolean moderation;

    private Integer moderationCount;

    private Boolean settings;
}
