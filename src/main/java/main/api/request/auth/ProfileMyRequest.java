package main.api.request.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileMyRequest {

    private String name;
    private String email;
    private String password;
    private short removePhoto;
}
