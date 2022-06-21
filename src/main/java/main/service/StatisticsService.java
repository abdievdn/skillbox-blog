package main.service;

import lombok.AllArgsConstructor;
import main.Blog;
import main.api.response.general.StatisticsResponse;
import main.model.GlobalSettings;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.SettingsRepository;
import main.model.repository.UserRepository;
import main.service.enums.SettingsCode;
import main.service.utils.TimestampUtil;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final PostVoteService postVoteService;

    public StatisticsResponse getStatisticsMy(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (post.getUser().getId() != user.getId()) continue;
            statisticsExtraction(statisticsResponse, post);
        }
        return statisticsResponse;
    }

    public StatisticsResponse getStatisticsAll(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        GlobalSettings settings = settingsRepository.findByCode(SettingsCode.STATISTICS_IS_PUBLIC.name()).orElseThrow();
        if (settings.getValue().equals(Blog.NO_VALUE) && user.getIsModerator() != 1) return null;
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            statisticsExtraction(statisticsResponse, post);
        }
        return statisticsResponse;
    }

    private void statisticsExtraction(StatisticsResponse statisticsResponse, Post post) {
        if (post.getModerationStatus().equals(ModerationStatus.ACCEPTED)) {
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
