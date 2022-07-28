package main.service;

import lombok.AllArgsConstructor;
import main.api.response.general.CalendarResponse;
import main.model.Post;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

@Service
@AllArgsConstructor
public class CalendarService {

    private final PostService postService;

    public CalendarResponse getYears() {
        CalendarResponse calendarResponse = new CalendarResponse();
        TreeSet<Integer> calendarYears = new TreeSet<>();
        Map<String, Integer> calendarPosts = new TreeMap<>();
        for (Post post : postService.findAllPosts()) {
            if (!postService.isActualPost(post)) continue;
            LocalDateTime postDate = post.getTime();
            calendarYears.add(postDate.getYear());
            String postDateFormat = postDate.toLocalDate().toString();
            if (calendarPosts.containsKey(postDateFormat)) {
                calendarPosts.put(postDateFormat, calendarPosts.get(postDateFormat) + 1);
            } else {
                calendarPosts.put(postDateFormat, 1);
            }
        }
        calendarResponse.setYears(calendarYears);
        calendarResponse.setPosts(calendarPosts);
        return calendarResponse;
    }
}