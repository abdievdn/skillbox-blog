package main.api.request.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileMyRequest {
    @NotNull
    private String name;
    @NotNull
    private String email;
    private String password;
    private short removePhoto;
}