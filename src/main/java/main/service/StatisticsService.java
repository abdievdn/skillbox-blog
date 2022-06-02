package main.service;

import main.api.response.StatisticsResponse;
import main.model.GlobalSettings;
import main.model.Post;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.SettingsRepository;
import main.model.repository.UserRepository;
import main.service.util.TimestampUtil;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class StatisticsService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;

    public StatisticsService(PostRepository postRepository, UserRepository userRepository, SettingsRepository settingsRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
    }

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
        if (settings.getValue().equals("NO") && user.getIsModerator() != 1) return null;
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setFirstPublication(0);
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            statisticsExtraction(statisticsResponse, post);
        }
        return statisticsResponse;
    }

    private void statisticsExtraction(StatisticsResponse statisticsResponse, Post post) {
        statisticsResponse.setPostsCount(statisticsResponse.getPostsCount() + 1);
        statisticsResponse.setLikesCount(0); //todo
        statisticsResponse.setDislikesCount(0); //todo
        statisticsResponse.setViewsCount(post.getViewCount());
        long earlierPublication = TimestampUtil.encode(post.getTime());
        if (earlierPublication < statisticsResponse.getFirstPublication() || statisticsResponse.getFirstPublication() == 0) {
            statisticsResponse.setFirstPublication(earlierPublication);
        }
    }
}
