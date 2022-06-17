package main.api.response.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.api.response.BlogResponse;

@Data
public class SettingsResponse implements BlogResponse {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiuserMode;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;
}
