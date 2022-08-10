package main.service;

import lombok.AllArgsConstructor;
import main.Blog;
import main.api.request.general.PostModerationRequest;
import main.api.request.post.*;
import main.api.request.post.enums.PostModerationDecision;
import main.api.request.post.enums.PostRequestStatus;
import main.api.response.auth.UserResponse;
import main.api.response.general.PostModerationResponse;
import main.api.response.post.*;
import main.controller.advice.ErrorsNum;
import main.controller.advice.ErrorsResponseException;
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
    private final TagRepository tagRepository;
    private final UserService userService;
    private final PostCommentService postCommentService;
    private final PostVoteService postVoteService;
    private final SettingsService settingsService;

    public PostsResponse getPosts(PostRequest postRequest) {
        return createPostsResponse(postRequest, getPostsList(postRequest, "", false));
    }

    public PostsResponse getPostsMy(PostRequest postRequest, Principal principal) {
        return createPostsResponse(postRequest, getPostsList(postRequest, principal.getName(), false));
    }

    public PostsResponse getPostsModeration(PostRequest postRequest, Principal principal) {
        return createPostsResponse(postRequest, getPostsList(postRequest, principal.getName(), true));
    }

    public PostResponse getPostById(int id, Principal principal) {
        Post post = postRepository.findById(id).orElseThrow();
        // view count is not increases if author or moderator is viewer
        checkPostViewer(principal, post);
        return createPostResponseId(post);
    }

    public PostModerationResponse postModerate(PostModerationRequest postModerationRequest, Principal principal) {
        User user = userService.findUser(principal.getName());
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
        post.setModerator(user);
        postRepository.save(post);
        PostModerationResponse postModerationResponse = new PostModerationResponse();
        postModerationResponse.setResult(true);
        return postModerationResponse;
    }

    public PostAddEditResponse addPost(PostAddEditRequest postAddEditRequest, Principal principal)
            throws ErrorsResponseException {
        return createOrUpdatePost(postAddEditRequest, principal.getName(), new Post(), principal);
    }

    public PostAddEditResponse editPost(PostAddEditRequest postAddEditRequest, int ID, Principal principal) throws ErrorsResponseException {
        Post post = postRepository.findById(ID).orElseThrow();
        return createOrUpdatePost(postAddEditRequest, String.valueOf(post.getUser().getId()), post, principal);
    }

    public boolean isActualPost(Post post) {
        return post.getModerationStatus().equals(ModerationStatus.ACCEPTED) && isActivePost(post);
    }

    public int getPostsCount() {
        int count = 0;
        for (Post post : findAllPosts()) {
            if (!isActualPost(post)) {
                continue;
            }
            count++;
        }
        return count;
    }

    public int getPostsToTagCount(Tag tag) {
        return (int) tag.getPosts().stream()
                .filter(this::isActualPost)
                .count();
    }

    public Iterable<Post> findAllPosts() {
        return postRepository.findAll();
    }

    private List<PostResponse> getPostsList(PostRequest postRequest, String userId, boolean moderate) {
        checkPostRequestParams(postRequest);
        List<PostResponse> posts = new CopyOnWriteArrayList<>();
        for (Post post : findAllPosts()) {
            if (!isActualPost(post) && postRequest.getStatus() == null) {
                continue;
            }
            if (isAllMatch(postRequest) ||
                    isQueryMatch(postRequest, post) ||
                    isDateMatch(postRequest, post) ||
                    isTagMatch(postRequest, post) ||
                    isStatusMatchUser(postRequest, post, userId) && !moderate ||
                    isStatusMatchModerator(postRequest, post, userId) && moderate) {
                posts.add(createPostResponse(post));
            }
        }
        return posts;
    }

    private boolean isAllMatch(PostRequest postRequest) {
        return postRequest.getQuery() == null && postRequest.getDate() == null && postRequest.getTag() == null && postRequest.getStatus() == null;
    }

    private boolean isQueryMatch(PostRequest postRequest, Post post) {
        return postRequest.getQuery() != null && (post.getText().contains(postRequest.getQuery()) || post.getTitle().contains(postRequest.getQuery()));
    }

    private boolean isDateMatch(PostRequest postRequest, Post post) {
        return postRequest.getDate() != null && post.getTime().toLocalDate().toString().equals(postRequest.getDate());
    }

    private boolean isTagMatch(PostRequest postRequest, Post post) {
        return postRequest.getTag() != null && post.getTags().stream().anyMatch(t -> t.getName().equals(postRequest.getTag()));
    }

    private boolean isStatusMatch(PostRequest postRequest, Post post) {
        return postRequest.getStatus() != null && isActivePost(post) &&
                post.getModerationStatus().equals(PostRequestStatus.valueOf(postRequest.getStatus().toUpperCase()).getModerationStatus());
    }

    private boolean isStatusMatchUser(PostRequest postRequest, Post post, String userId) {
        return String.valueOf(post.getUser().getId()).equals(userId) &&
                (isStatusMatch(postRequest, post) || !isActivePost(post) && postRequest.getStatus().equalsIgnoreCase(PostRequestStatus.INACTIVE.name()));

    }

    private boolean isStatusMatchModerator(PostRequest postRequest, Post post, String userId) {
        return post.getModerator() == null && isStatusMatch(postRequest, post) ||
                post.getModerator() != null && String.valueOf(post.getModerator().getId()).equals(userId) && isStatusMatch(postRequest, post);
    }

    private boolean isActivePost(Post post) {
        return post.getIsActive() == 1;
    }

    private PostsResponse createPostsResponse(PostRequest postRequest, List<PostResponse> posts) {
        PostsResponse postsResponse = new PostsResponse();
        sortPostsByMode(postRequest.getMode(), posts);
        postsResponse.setPosts(postsSublist(postRequest.getOffset(), postRequest.getLimit(), posts));
        postsResponse.setCount(posts.size());
        return postsResponse;
    }

    private PostResponse createPostResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setTimestamp(TimestampUtil.encode(post.getTime()));
        UserResponse user = createUserResponseFromPost(post);
        postResponse.setUser(user);
        postResponse.setTitle(post.getTitle());
        postResponse.setAnnounce(announce(post.getText()));
        postResponse.setLikeCount(postVoteService.getPostVoteCount(post.getId(), (short) 1));
        postResponse.setDislikeCount(postVoteService.getPostVoteCount(post.getId(), (short) -1));
        postResponse.setCommentCount(postCommentService.getCommentsCount(post.getId()));
        postResponse.setViewCount(post.getViewCount());
        return postResponse;
    }

    private PostResponse createPostResponseId(Post post) {
        PostResponse postResponse = createPostResponse(post);
        postResponse.setAnnounce(null);
        postResponse.setCommentCount(null);
        postResponse.setText(post.getText());
        List<PostCommentResponse> commentsToPost = postCommentService.getComments(post.getId());
        postResponse.setComments(commentsToPost);
        Set<String> tagsToPost = new CopyOnWriteArraySet<>();
        for (Tag tag : post.getTags()) {
            tagsToPost.add(tag.getName());
        }
        postResponse.setTags(tagsToPost);
        return postResponse;
    }

    private UserResponse createUserResponseFromPost(Post post) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(post.getUser().getId());
        userResponse.setName(post.getUser().getName());
        return userResponse;
    }

    private PostAddEditResponse createOrUpdatePost(PostAddEditRequest postAddEditRequest, String author, Post post, Principal principal) throws ErrorsResponseException {
        if (isIncorrectText(postAddEditRequest.getTitle(), Blog.POST_MIN_TITLE_LENGTH)) {
            throw new ErrorsResponseException(ErrorsNum.TITLE);
        }
        if (isIncorrectText(postAddEditRequest.getText(), Blog.POST_MIN_TEXT_LENGTH)) {
            throw new ErrorsResponseException(ErrorsNum.TEXT);
        }
        User user = userService.findUser(author);
        User authorizedUser = userService.findUser(principal.getName());
        post.setUser(user);
        post.setIsActive(postAddEditRequest.getActive());
        post.setTitle(postAddEditRequest.getTitle());
        post.setText(postAddEditRequest.getText());
        if (authorizedUser.getIsModerator() != 1 && user.getIsModerator() != 1 && settingsService.getGlobalSettings().isPostPremoderation()) {
            post.setModerationStatus(ModerationStatus.NEW);
        } else {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }
        post.setTime(getLocalDateTime(postAddEditRequest.getTimestamp()));
        post.setTags(getTagsToPost(postAddEditRequest.getTags()));
        postRepository.save(post);
        PostAddEditResponse postAddEditResponse = new PostAddEditResponse();
        postAddEditResponse.setResult(true);
        return postAddEditResponse;
    }

    private Set<Tag> getTagsToPost(Set<String> tags) {
        Set<Tag> refreshTags = new HashSet<>();
        tags.forEach(t -> {
            Tag tag = tagRepository.findByName(t).orElseGet(Tag::new);
            if (tag.getName() == null) {
                tag.setName(t);
            }
            refreshTags.add(tag);
        });
        return refreshTags;
    }

    private LocalDateTime getLocalDateTime(Long timestamp) {
        LocalDateTime addedTime = LocalDateTime.now();
        long nowTimestamp = TimestampUtil.encode(addedTime);
        long postTimestamp = timestamp;
        if (postTimestamp > nowTimestamp) {
            addedTime = TimestampUtil.decode(postTimestamp);
        }
        return addedTime;
    }

    private boolean isIncorrectText(String text, int textLength) {
        return text.isEmpty() || text.length() < textLength;
    }

    private String announce(String text) {
        String announceText = text.length() > Blog.POST_ANNOUNCE_MAX_TEXT_LENGTH ?
                text.substring(0, Blog.POST_ANNOUNCE_MAX_TEXT_LENGTH) : text;
        return Jsoup.parse(announceText).text().concat("...");
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

    private void checkPostRequestParams(PostRequest postRequest) {
        if (postRequest.getLimit() == 0) {
            postRequest.setLimit(10);
        }
        if (postRequest.getMode() == null) {
            postRequest.setMode(PostSortOrder.RECENT.name());
        }
    }

    private void checkPostViewer(Principal principal, Post post) {
        if (principal != null) {
            User user = userService.findUser(principal.getName());
            if (!user.getEmail().equals(post.getUser().getEmail()) && user.getIsModerator() != 1) {
                post.setViewCount(post.getViewCount() + 1);
            }
        } else {
            post.setViewCount(post.getViewCount() + 1); // view increases if unregistered user
        }
        postRepository.save(post);
    }
}