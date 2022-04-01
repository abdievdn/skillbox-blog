package main.service;

import main.model.ModerationStatus;
import main.model.Post;
import main.response.CalendarResponse;
import main.response.PostResponse;
import main.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    Iterable<Post> posts;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
        posts = postRepository.findAll();
    }

    private static final Comparator<PostResponse.PostsInPostResponse>
            COMPARE_BY_TIME = (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()),
            COMPARE_BY_LIKE_COUNT = (o1, o2) -> Integer.compare(o2.getLikeCount(), o1.getLikeCount()),
            COMPARE_BY_COMMENT_COUNT = (o1, o2) -> Integer.compare(o2.getCommentCount(), o1.getCommentCount());

    public PostResponse getActualPosts(String offset, String limit, String mode) {
        PostResponse postResponse = new PostResponse();
        List<PostResponse.PostsInPostResponse> postsInPostResponse = actualPostsList(postResponse);
        sortPostsByMode(mode, postsInPostResponse);
        postResponse.setPosts(postsSublist(offset, limit, postsInPostResponse));
        return postResponse;
    }

    public PostResponse searchPosts(String offset, String limit, String query) {
        PostResponse postResponse = new PostResponse();
        List<PostResponse.PostsInPostResponse> postsInPostResponse = actualPostsList(postResponse);
        for (PostResponse.PostsInPostResponse postInPostResponse : postsInPostResponse) {
            Optional<Post> post = postRepository.findById(postInPostResponse.getId());
            if (post.isPresent()) {
                String text = post.get().getText();
                if (!text.contains(query)) {
                    postsInPostResponse.remove(postInPostResponse);
                    postResponse.setCount(postResponse.getCount() - 1);
                }
            }
        }
        sortPostsByMode("recent", postsInPostResponse);
        postResponse.setPosts(postsSublist(offset, limit, postsInPostResponse));
        return postResponse;
    }

    public CalendarResponse getYears() {
        CalendarResponse calendarResponse = new CalendarResponse();
        TreeSet<Integer> calendarYears = new TreeSet<>();
        Map<String, Integer> calendarPosts = new TreeMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (Post post : posts) {
            if (!isActual(post)) {
                continue;
            }
            Date postDate = post.getTime();
            calendarYears.add(postDate.getYear());
            String postDateFormat = formatter.format(postDate);
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

    private List<PostResponse.PostsInPostResponse> actualPostsList(PostResponse postResponse) {
        int count = 0;
        List<PostResponse.PostsInPostResponse> postsInPostResponse = postResponse.getPosts();
        for (Post post : posts) {
            if (!isActual(post)) {
                continue;
            }
            PostResponse.PostsInPostResponse postInPostResponse = new PostResponse.PostsInPostResponse();
            postInPostResponse.setId(post.getId());
            postInPostResponse.setTimestamp(post.getTime().toInstant().toEpochMilli() / 1000);
            PostResponse.PostsInPostResponse.UserInPostResponse userInPostResponse =
                    new PostResponse.PostsInPostResponse.UserInPostResponse();
            userInPostResponse.setId(post.getUser().getId());
            userInPostResponse.setName(post.getUser().getName());
            postInPostResponse.setUser(userInPostResponse);
            postInPostResponse.setTittle(post.getTitle());
            postInPostResponse.setAnnounce(announce(post.getText()));
            postInPostResponse.setLikeCount(0);
            postInPostResponse.setDislikeCount(0);
            postInPostResponse.setCommentCount(0);
            postInPostResponse.setViewCount(post.getViewCount());
            postsInPostResponse.add(postInPostResponse);
            count++;
        }
        postResponse.setCount(count);
        return postsInPostResponse;
    }

    private boolean isActual(Post post) {
        return post.getModerationStatus().equals(ModerationStatus.ACCEPTED) && post.isActive();
    }

    private String announce(String text) {
        return text.substring(0, 150).concat("...");
    }

    private void sortPostsByMode(String mode, List<PostResponse.PostsInPostResponse> postsInPostResponse) {
        switch (mode) {
            case "recent":
                postsInPostResponse.sort(COMPARE_BY_TIME);
                break;
            case "early":
                postsInPostResponse.sort(Collections.reverseOrder(COMPARE_BY_TIME));
                break;
            case "best":
                postsInPostResponse.sort(COMPARE_BY_LIKE_COUNT);
                break;
            case "popular":
                postsInPostResponse.sort(COMPARE_BY_COMMENT_COUNT);
                break;
            default:
                break;
        }
    }

    private List<PostResponse.PostsInPostResponse> postsSublist(String offset, String limit, List<PostResponse.PostsInPostResponse> posts) {
        int offsetNumber = Integer.parseInt(offset);
        int limitNumber = Integer.parseInt(limit);
        int countNumber = offsetNumber + limitNumber;
        if (countNumber >= posts.size()) {
            countNumber = posts.size();
        }
        return posts.subList(offsetNumber, countNumber);
    }
}
