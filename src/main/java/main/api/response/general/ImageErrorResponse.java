package main.api.response.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageErrorResponse {
    private String image;

    @Data
    public static class SettingsResponse {

        @JsonProperty("MULTIUSER_MODE")
        private boolean multiuserMode;

        @JsonProperty("POST_PREMODERATION")
        private boolean postPremoderation;

        @JsonProperty("STATISTICS_IS_PUBLIC")
        private boolean statisticsIsPublic;

    }

    @Data
    public static class StatisticsResponse {
        private int
                postsCount,
                likesCount,
                dislikesCount,
                viewsCount;
        private long firstPublication;
    }
}
