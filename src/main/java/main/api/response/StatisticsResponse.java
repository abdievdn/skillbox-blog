package main.api.response;

import lombok.Data;

@Data
public class StatisticsResponse {
    private int
            postsCount,
            likesCount,
            dislikesCount,
            viewsCount;
    private long firstPublication;
}
