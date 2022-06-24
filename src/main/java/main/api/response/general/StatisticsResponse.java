package main.api.response.general;

import lombok.Data;

@Data
public class StatisticsResponse {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;
}
