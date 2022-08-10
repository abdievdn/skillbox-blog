package main.service;

import lombok.AllArgsConstructor;
import main.Blog;
import main.api.response.statistics.StatisticsResponse;
import main.model.GlobalSettings;
import main.model.Post;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.SettingsRepository;
import main.service.enums.SettingsCode;
import main.service.utils.TimestampUtil;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    private final SettingsRepository settingsRepository;
    private final UserService userService;
    private final PostService postService;
    private final PostVoteService postVoteService;

    public StatisticsResponse getStatisticsMy(Principal principal) {
        User user = userService.findUser(principal.getName());
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (post.getUser().getId() != user.getId()) continue;
            createStatisticsResponse(statisticsResponse, post);
        }
        return statisticsResponse;
    }

    public StatisticsResponse getStatisticsAll(Principal principal) {
        GlobalSettings statisticsIsPublic = settingsRepository.findByCode(SettingsCode.STATISTICS_IS_PUBLIC.name()).orElseThrow();
        if (statisticsIsPublic.getValue().equals(Blog.NO_VALUE)) {
            if (principal != null) {
                User user = userService.findUser(principal.getName());
                if (user.getIsModerator() != 1) return null;
            } else {
                return null;
            }
        }
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        for (Post post : postService.findAllPosts()) {
            createStatisticsResponse(statisticsResponse, post);
        }
        return statisticsResponse;
    }

    private void createStatisticsResponse(StatisticsResponse statisticsResponse, Post post) {
        if (postService.isActualPost(post)) {
            statisticsResponse.setPostsCount(statisticsResponse.getPostsCount() + 1);
            statisticsResponse.setLikesCount(postVoteService.getPostVoteCount(post.getId(), (short) 1) + statisticsResponse.getLikesCount());
            statisticsResponse.setDislikesCount(postVoteService.getPostVoteCount(post.getId(), (short) -1) + statisticsResponse.getDislikesCount());
            statisticsResponse.setViewsCount(post.getViewCount() + statisticsResponse.getViewsCount());
            long earlierPublication = TimestampUtil.encode(post.getTime());
            if (earlierPublication < statisticsResponse.getFirstPublication() || statisticsResponse.getFirstPublication() == 0) {
                statisticsResponse.setFirstPublication(earlierPublication);
            }
        }
    }
}