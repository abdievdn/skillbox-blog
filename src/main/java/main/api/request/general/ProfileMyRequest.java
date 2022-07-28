package main.api.request.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileMyRequest {
    private String name;
    private String email;
    private String password;
    private short removePhoto;
}
