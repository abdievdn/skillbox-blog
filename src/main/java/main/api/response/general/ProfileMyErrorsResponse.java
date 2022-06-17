package main.api.response.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileMyErrorsResponse {
    private String email;
    private String photo;
    private String name;
    private String password;
}
