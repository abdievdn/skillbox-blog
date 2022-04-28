package main.service;

import main.api.response.*;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostComment;
import main.model.Tag;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostRepository;
import main.api.request.PostRequest;
import main.api.request.RequestKey;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class PostService {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String
            RECENT = "recent",
            EARLY = "early",
            BEST = "best",
            POPULAR = "popular";

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    private static final Comparator<PostResponse>
            COMPARE_BY_TIME = (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()),
            COMPARE_BY_LIKE_COUNT = (o1, o2) -> Integer.compare(o2.getLikeCount(), o1.getLikeCount()),
            COMPARE_BY_COMMENT_COUNT = (o1, o2) -> Integer.compare(o2.getCommentCount(), o1.getCommentCount());

    public PostService(PostRepository postRepository, PostCommentRepository postCommentRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
    }

    public PostsResponse getActualPosts(PostRequest postRequest) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse);
        sortPostsByMode(postRequest.getMode(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse searchPosts(PostRequest postRequest, RequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, postRequest.getQuery(), key);
        sortPostsByMode(RECENT, posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsByDate(PostRequest postRequest, RequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, postRequest.getDate(), key);
        sortPostsByMode(RECENT, posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsByTag(PostRequest postRequest, RequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, postRequest.getTag(), key);
        sortPostsByMode(RECENT, posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostByIdResponse getPostById(int id) {
        PostByIdResponse postByIdResponse = new PostByIdResponse();
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postByIdResponse.setId(id);
            postByIdResponse.setTimestamp(calculateTimestamp(post.get().getTime()));
            UserResponse user = new UserResponse();
            user.setId(post.get().getUser().getId());
            user.setName(post.get().getUser().getName());
            postByIdResponse.setUser(user);
            postByIdResponse.setTittle(post.get().getTitle());
            postByIdResponse.setText(post.get().getText());
            postByIdResponse.setLikeCount(0); // todo
            postByIdResponse.setDislikeCount(0); //todo
            postByIdResponse.setViewCount(0); // todo

            PostCommentResponse postCommentResponse = new PostCommentResponse();
            List<PostCommentResponse> commentsToPost = new CopyOnWriteArrayList<>();
            Iterable<PostComment> comments = postCommentRepository.findAll();
            for (PostComment comment : comments) {
                if (comment.getPost().getId() == post.get().getId()) {
                    postCommentResponse.setId(comment.getId());
                    postCommentResponse.setTimestamp(calculateTimestamp(comment.getTime()));
                    postCommentResponse.setText(comment.getText());
                    UserResponse userToComment = new UserResponse();
                    userToComment.setId(comment.getUser().getId());
                    userToComment.setName(comment.getUser().getName());
                    userToComment.setPhoto(comment.getUser().getPhoto());
                    postCommentResponse.setUser(userToComment);
                    commentsToPost.add(postCommentResponse);
                }
            }
            postByIdResponse.setComments(commentsToPost);
            Set<String> tagsToPost = new CopyOnWriteArraySet<>();
            for (Tag tag : post.get().getTags()) {
                tagsToPost.add(tag.getName());
            }
            postByIdResponse.setTags(tagsToPost);
        }
        return postByIdResponse;
    }

    public CalendarResponse getYears() {
        CalendarResponse calendarResponse = new CalendarResponse();
        TreeSet<Integer> calendarYears = new TreeSet<>();
        Map<String, Integer> calendarPosts = new TreeMap<>();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (!isActual(post)) {
                continue;
            }
            LocalDateTime postDate = post.getTime();
            calendarYears.add(postDate.getYear());
            String postDateFormat = DATE_FORMAT.format(postDate);
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

    private List<PostResponse> actualPosts(PostsResponse postsResponse) {
        int count = 0;
        List<PostResponse> posts = new CopyOnWriteArrayList<>();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (!isActual(post)) {
                continue;
            }
            postExtraction(posts, post);
            count++;
        }
        postsResponse.setCount(count);
        return posts;
    }

    private List<PostResponse> actualPosts(PostsResponse postsResponse, String value, RequestKey key) {
        int count = 0;
        List<PostResponse> posts = new CopyOnWriteArrayList<>();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (!isActual(post)) {
                continue;
            }

            if (key.equals(RequestKey.SEARCH)) {
                String text = post.getText().toLowerCase();
                if (!text.contains(value.toLowerCase())) {
                    continue;
                }
            }
            if (key.equals(RequestKey.DATE)) {
                String dateFromPost = DATE_FORMAT.format(post.getTime());
                if (!value.equals(dateFromPost)) {
                    continue;
                }
            }
            if (key.equals(RequestKey.TAG)) {
                boolean noTag = true;
                for (Tag tag : post.getTags()) {
                    if (value.equals(tag.getName())) {
                        noTag = false;
                        break;
                    }
                }
                if (noTag) {
                    continue;
                }
            }
            postExtraction(posts, post);
            count++;
        }
        postsResponse.setCount(count);
        return posts;
    }

    private void postExtraction(List<PostResponse> posts, Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setTimestamp(calculateTimestamp(post.getTime()));
        UserResponse user = new UserResponse();
        user.setId(post.getUser().getId());
        user.setName(post.getUser().getName());
        postResponse.setUser(user);
        postResponse.setTittle(post.getTitle());
        postResponse.setAnnounce(announce(post.getText(), 0, 150));
        postResponse.setLikeCount(0); // todo
        postResponse.setDislikeCount(0); //todo
        postResponse.setCommentCount(0); //todo
        postResponse.setViewCount(post.getViewCount());
        posts.add(postResponse);
    }

    private long calculateTimestamp(LocalDateTime time) {
        ZonedDateTime zdt = ZonedDateTime.of(time, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli() / 1000;
    }

    private boolean isActual(Post post) {
        return post.getModerationStatus().equals(ModerationStatus.ACCEPTED) && post.isActive();
    }

    private String announce(String text, int start, int end) {
        return text.substring(start, end).concat("...");
    }

    private void sortPostsByMode(String mode, List<PostResponse> posts) {
        switch (mode) {
            case RECENT:
                posts.sort(COMPARE_BY_TIME);
                break;
            case EARLY:
                posts.sort(Collections.reverseOrder(COMPARE_BY_TIME));
                break;
            case BEST:
                posts.sort(COMPARE_BY_LIKE_COUNT);
                break;
            case POPULAR:
                posts.sort(COMPARE_BY_COMMENT_COUNT);
                break;
            default:
                break;
        }
    }

    private List<PostResponse> postsSublist(int offset, int limit, List<PostResponse> posts) {
        int count = offset + limit;
        if (count >= posts.size()) {
            count = posts.size();
        }
        return posts.subList(offset, count);
    }
}
