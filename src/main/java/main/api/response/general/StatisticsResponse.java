package main.api.response.general;

import lombok.Data;
import main.api.response.BlogResponse;

@Data
public class StatisticsResponse implements BlogResponse {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;
}
