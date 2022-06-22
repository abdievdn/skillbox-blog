package main.service;

import lombok.AllArgsConstructor;
import main.Blog;
import main.api.request.general.PostModerationRequest;
import main.api.request.post.*;
import main.api.request.post.enums.PostModerationDecision;
import main.api.request.post.enums.PostRequestKey;
import main.api.request.post.enums.PostRequestStatus;
import main.api.response.auth.UserResponse;
import main.api.response.general.PostModerationResponse;
import main.api.response.post.*;
import main.controller.advice.error.PostAddEditError;
import main.controller.advice.exception.PostAddEditException;
import main.model.*;
import main.model.repository.*;
import main.service.enums.PostSortOrder;
import main.service.utils.TimestampUtil;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final PostCommentService postCommentService;
    private final PostVoteService postVoteService;
    private final SettingsService settingsService;

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
        sortPostsByMode(PostSortOrder.RECENT.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsByDate(PostRequest postRequest, PostRequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, postRequest.getDate(), key);
        sortPostsByMode(PostSortOrder.RECENT.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsByTag(PostRequest postRequest, PostRequestKey key) {
        PostsResponse postsResponse = new PostsResponse();
        List<PostResponse> posts = actualPosts(postsResponse, postRequest.getTag(), key);
        sortPostsByMode(PostSortOrder.RECENT.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsMy(PostRequest postRequest, Principal principal) {
        int count = 0;
        PostsResponse postsResponse = new PostsResponse();
        String authorizedUser = principal.getName();
        List<PostResponse> posts = new CopyOnWriteArrayList<>();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (!post.getUser().getEmail().equals(authorizedUser)) continue;
            switch (PostRequestStatus.valueOf(postRequest.getStatus().toUpperCase())) {
                case INACTIVE:
                    if (post.getIsActive() == 1) continue;
                    break;
                case PENDING:
                    if (post.getIsActive() != 1) continue;
                    if (!post.getModerationStatus().equals(ModerationStatus.NEW)) continue;
                    break;
                case DECLINED:
                    if (post.getIsActive() != 1) continue;
                    if (!post.getModerationStatus().equals(ModerationStatus.DECLINED)) continue;
                    break;
                case PUBLISHED:
                    if (post.getIsActive() != 1) continue;
                    if (!post.getModerationStatus().equals(ModerationStatus.ACCEPTED)) continue;
                    break;
                default:
                    break;
            }
            postExtraction(posts, post);
            count++;
        }
        postsResponse.setCount(count);
        sortPostsByMode(PostSortOrder.RECENT.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostsResponse getPostsModeration(PostRequest postRequest, Principal principal) {
        int count = 0;
        PostsResponse postsResponse = new PostsResponse();
        String authorizedUser = principal.getName();
        List<PostResponse> posts = new CopyOnWriteArrayList<>();
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (post.getModerator() != null && !post.getModerator().getEmail().equals(authorizedUser)) continue;
            switch (PostRequestStatus.valueOf(postRequest.getStatus().toUpperCase())) {
                case NEW:
                    if (post.getIsActive() != 1) continue;
                    if (!post.getModerationStatus().equals(ModerationStatus.NEW)) continue;
                    break;
                case ACCEPTED:
                    if (post.getIsActive() != 1) continue;
                    if (!post.getModerationStatus().equals(ModerationStatus.ACCEPTED)) continue;
                    break;
                case DECLINED:
                    if (post.getIsActive() != 1) continue;
                    if (!post.getModerationStatus().equals(ModerationStatus.DECLINED)) continue;
                    break;
                default:
                    break;
            }
            postExtraction(posts, post);
            count++;
        }
        postsResponse.setCount(count);
        sortPostsByMode(PostSortOrder.RECENT.name(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        return postsResponse;
    }

    public PostByIdResponse getPostById(int id, Principal principal) {
        PostByIdResponse postByIdResponse = new PostByIdResponse();
        Post post = postRepository.findById(id).orElseThrow();
// view count not increase if author or moderator is viewer
        if (principal != null) {
            if (!principal.getName().equals(post.getUser().getEmail())) {
                Optional<User> existUser = userRepository.findByEmail(principal.getName());
                if (existUser.isPresent() && existUser.get().getIsModerator() != 1) {
                    post.setViewCount(post.getViewCount() + 1);
                }
            }
        } else {
            post.setViewCount(post.getViewCount() + 1);
        }
        postRepository.save(post);
        postByIdResponse.setId(id);
        postByIdResponse.setTimestamp(TimestampUtil.encode(post.getTime()));
        UserResponse user = getUserFromPost(post);
        postByIdResponse.setUser(user);
        postByIdResponse.setTitle(post.getTitle());
        postByIdResponse.setText(post.getText());
        postByIdResponse.setLikeCount(postVoteService.getPostVoteCount(post.getId(), (short) 1));
        postByIdResponse.setDislikeCount(postVoteService.getPostVoteCount(post.getId(), (short) -1));
        postByIdResponse.setViewCount(post.getViewCount());
        List<PostCommentResponse> commentsToPost = postCommentService.getComments(post.getId());
        postByIdResponse.setComments(commentsToPost);
        Set<String> tagsToPost = new CopyOnWriteArraySet<>();
        for (Tag tag : post.getTags()) {
            tagsToPost.add(tag.getName());
        }
        postByIdResponse.setTags(tagsToPost);
        return postByIdResponse;
    }

    private UserResponse getUserFromPost(Post post) {
        UserResponse user = new UserResponse();
        user.setId(post.getUser().getId());
        user.setName(post.getUser().getName());
        return user;
    }

    public PostAddEditResponse editPost(PostAddEditRequest postAddEditRequest, int ID, Principal principal) throws PostAddEditException {
        Post post = postRepository.findById(ID).orElseThrow();
        String author = post.getUser().getEmail();
        return createUpdatePost(postAddEditRequest, author, post, principal);
    }

    public PostAddEditResponse addPost(PostAddEditRequest postAddEditRequest, Principal principal)
            throws PostAddEditException {
        String author = principal.getName();
        return createUpdatePost(postAddEditRequest, author, new Post(), principal);
    }

    private PostAddEditResponse createUpdatePost(PostAddEditRequest postAddEditRequest, String author, Post post, Principal principal) throws PostAddEditException {
        if (isIncorrectText(postAddEditRequest.getTitle(), Blog.POST_MIN_TITLE_LENGTH)) {
            throw new PostAddEditException(PostAddEditError.TITLE);
        }
        if (isIncorrectText(postAddEditRequest.getText(), Blog.POST_MIN_TEXT_LENGTH)) {
            throw new PostAddEditException(PostAddEditError.TEXT);
        }
        PostAddEditResponse postAddEditResponse = new PostAddEditResponse();
        User user = userRepository.findByEmail(author).orElseThrow();
        User authorizedUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        post.setUser(user);
        post.setIsActive(postAddEditRequest.getActive());
        post.setTitle(postAddEditRequest.getTitle());
        post.setText(postAddEditRequest.getText());
        if (authorizedUser.getIsModerator() != 1 && user.getIsModerator() != 1 && settingsService.getGlobalSettings().isPostPremoderation()) {
            post.setModerationStatus(ModerationStatus.NEW);
        } else {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }
// compare timestamp
        LocalDateTime addedTime = LocalDateTime.now();
        long nowTimestamp = TimestampUtil.encode(addedTime);
        long postTimestamp = postAddEditRequest.getTimestamp();
        if (postTimestamp > nowTimestamp) {
            addedTime = TimestampUtil.decode(postTimestamp);
        }
        post.setTime(addedTime);
// add tags, checks new or existing tag
        if (postAddEditRequest.getTags().length != 0) {
            Set<Tag> tags = new HashSet<>();
            String[] requestTags = postAddEditRequest.getTags();
            for (String requestTag : requestTags) {
                Optional<Tag> tag = tagRepository.findByName(requestTag);
                if (tag.isPresent()) {
                    tags.add(tag.get());
                } else {
                    Tag newTag = new Tag();
                    newTag.setName(requestTag);
                    tags.add(newTag);
                }
            }
            post.setTags(tags);
        } else post.setTags(new HashSet<>());
        postAddEditResponse.setResult(true);
        postRepository.save(post);
        return postAddEditResponse;
    }

    public PostModerationResponse postModerate(PostModerationRequest postModerationRequest, Principal principal) {
        String authorizedUser = principal.getName();
        User user = userRepository.findByEmail(authorizedUser).orElseThrow();
        if (user.getIsModerator() != 1) return null;
        PostModerationResponse postModerationResponse = new PostModerationResponse();
        Post post = postRepository.findById(postModerationRequest.getPostId()).orElseThrow();
        switch (PostModerationDecision.valueOf(postModerationRequest.getDecision().toUpperCase())) {
            case ACCEPT:
                post.setModerationStatus(ModerationStatus.ACCEPTED);
                break;
            case DECLINE:
                post.setModerationStatus(ModerationStatus.DECLINED);
                break;
            default:
                break;
        }
        postRepository.save(post);
        postModerationResponse.setResult(true);
        return postModerationResponse;
    }

    private boolean isIncorrectText(String text, int textLength) {
        return text.isEmpty() || text.length() < textLength;
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
                    boolean tagFound = false;
                    for (Tag tag : post.getTags()) {
                        if (value.equals(tag.getName())) {
                            tagFound = true;
                            break;
                        }
                    }
                    if (!tagFound) continue;
                default:
                    break;
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
        postResponse.setTimestamp(TimestampUtil.encode(post.getTime()));
        UserResponse user = getUserFromPost(post);
        postResponse.setUser(user);
        postResponse.setTitle(post.getTitle());
        postResponse.setAnnounce(announce(post.getText()));
        postResponse.setLikeCount(postVoteService.getPostVoteCount(post.getId(), (short) 1));
        postResponse.setDislikeCount(postVoteService.getPostVoteCount(post.getId(), (short) -1));
        postResponse.setCommentCount(postCommentService.getCommentsCount(post.getId()));
        postResponse.setViewCount(post.getViewCount());
        posts.add(postResponse);
    }

    public static boolean isNotActual(Post post) {
        return !post.getModerationStatus().equals(ModerationStatus.ACCEPTED) || post.getIsActive() != 1;
    }

    public int getNewPostsCount() {
        int postsCount = 0;
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (post.getModerationStatus().equals(ModerationStatus.NEW) && post.getIsActive() == 1) {
                postsCount++;
            }
        }
        return postsCount;
    }

    private String announce(String text) {
        String announceText = text.length() > Blog.POST_ANNOUNCE_MAX_TEXT_LENGTH ?
                text.substring(0, Blog.POST_ANNOUNCE_MAX_TEXT_LENGTH).concat("...") : text;
        return Jsoup.parse(announceText).text();
    }

    private void sortPostsByMode(String mode, List<PostResponse> posts) {
        switch (PostSortOrder.valueOf(mode.toUpperCase())) {
            case RECENT:
                posts.sort(Collections.reverseOrder(Comparator.comparing(PostResponse::getTimestamp)));
                break;
            case EARLY:
                posts.sort(Comparator.comparing(PostResponse::getTimestamp));
                break;
            case BEST:
                posts.sort(Collections.reverseOrder(Comparator.comparing(PostResponse::getLikeCount)));
                break;
            case POPULAR:
                posts.sort(Collections.reverseOrder(Comparator.comparing(PostResponse::getCommentCount)));
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
