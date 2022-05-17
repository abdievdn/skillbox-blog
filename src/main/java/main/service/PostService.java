package main.service;

import main.api.request.PostRequestStatus;
import main.api.response.*;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostComment;
import main.model.Tag;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostRepository;
import main.api.request.PostRequest;
import main.api.request.PostRequestKey;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    public PostService(PostRepository postRepository, PostCommentRepository postCommentRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
    }

    public PostsResponse getActualPosts(PostRequest postRequest, PostRequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, "", key);
        sortPostsByMode(postRequest.getMode(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse searchPosts(PostRequest postRequest, PostRequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, postRequest.getQuery(), key);
        sortPostsByMode(PostSortOrder.recent.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsByDate(PostRequest postRequest, PostRequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, postRequest.getDate(), key);
        sortPostsByMode(PostSortOrder.recent.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsByTag(PostRequest postRequest, PostRequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, postRequest.getTag(), key);
        sortPostsByMode(PostSortOrder.recent.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsMy(PostRequest postRequest) {
        int count = 0;
        PostsResponse postsResponse = new PostsResponse();
        String authorizedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PostResponse> posts = new CopyOnWriteArrayList<>();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post: postIterable) {
            if (!post.getUser().getEmail().equals(authorizedUser)) continue;
            switch (PostRequestStatus.valueOf(postRequest.getStatus())) {
                case inactive:
                    if (post.getIsActive() == 1) continue;
                    break;
                case pending:
                    if (!post.getModerationStatus().equals(ModerationStatus.NEW)) continue;
                    break;
                case declined:
                    if (!post.getModerationStatus().equals(ModerationStatus.DECLINED)) continue;
                    break;
                case published:
                    if (!post.getModerationStatus().equals(ModerationStatus.ACCEPTED)) continue;
                    break;
                default: break;
            }
            postExtraction(posts, post);
            count++;
        }
        postsResponse.setCount(count);
        sortPostsByMode(PostSortOrder.recent.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostByIdResponse getPostById(int id) {
        PostByIdResponse postByIdResponse = new PostByIdResponse();
        Post post = postRepository.findById(id).orElseThrow();
        postByIdResponse.setId(id);
        postByIdResponse.setTimestamp(calculateTimestamp(post.getTime()));
        UserResponse user = new UserResponse();
        user.setId(post.getUser().getId());
        user.setName(post.getUser().getName());
        postByIdResponse.setUser(user);
        postByIdResponse.setTittle(post.getTitle());
        postByIdResponse.setText(post.getText());
        postByIdResponse.setLikeCount(0); // todo
        postByIdResponse.setDislikeCount(0); //todo
        postByIdResponse.setViewCount(0); // todo
        PostCommentResponse postCommentResponse = new PostCommentResponse();
        List<PostCommentResponse> commentsToPost = new CopyOnWriteArrayList<>();
        Iterable<PostComment> comments = postCommentRepository.findAll();
        for (PostComment comment : comments) {
            if (comment.getPost().getId() == post.getId()) {
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
        for (Tag tag : post.getTags()) {
            tagsToPost.add(tag.getName());
        }
        postByIdResponse.setTags(tagsToPost);
        return postByIdResponse;
    }

    public CalendarResponse getYears() {
        CalendarResponse calendarResponse = new CalendarResponse();
        TreeSet<Integer> calendarYears = new TreeSet<>();
        Map<String, Integer> calendarPosts = new TreeMap<>();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (isNotActual(post)) continue;
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

    private List<PostResponse> actualPosts(PostsResponse postsResponse, String value, PostRequestKey key) {
        int count = 0;
        List<PostResponse> posts = new CopyOnWriteArrayList<>();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (isNotActual(post)) continue;
            switch (key) {
                case SEARCH:
                    String text = post.getText().toLowerCase();
                    if (!text.contains(value.toLowerCase())) continue;
                    break;
                case DATE:
                    if (!value.equals(post.getTime().toLocalDate().toString())) continue;
                    break;
                case TAG:
                    boolean noTag = true;
                    for (Tag tag : post.getTags()) {
                        if (value.equals(tag.getName())) {
                            noTag = false;
                            break;
                        }
                    }
                    if (noTag) continue;
                    break;
                default: break;
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
        postResponse.setAnnounce(announce(post.getText()));
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

    private boolean isNotActual(Post post) {
        return !post.getModerationStatus().equals(ModerationStatus.ACCEPTED) || post.getIsActive() !=1;
    }

    private String announce(String text) {
        return text.substring(0, 150).concat("...");
    }

    private void sortPostsByMode(String mode, List<PostResponse> posts) {
        switch (PostSortOrder.valueOf(mode)) {
            case recent:
                posts.sort(Collections.reverseOrder(Comparator.comparing(PostResponse::getTimestamp)));
                break;
            case early:
                posts.sort(Comparator.comparing(PostResponse::getTimestamp));
                break;
            case best:
                posts.sort(Comparator.comparing(PostResponse::getLikeCount));
                break;
            case popular:
                posts.sort(Comparator.comparing(PostResponse::getViewCount));
                break;
            default: break;
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
