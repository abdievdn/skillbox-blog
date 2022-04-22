package main.response;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckResponse {

    private boolean result;
    private RegisterResponse user;
}
