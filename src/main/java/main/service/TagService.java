package main.service;

import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import main.repository.PostRepository;
import main.repository.TagRepository;
import main.response.TagResponse;
import main.response.TagsResponse;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public TagService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public TagsResponse getTags() {
        int postsCount = getPostsCount();
        TagsResponse tagsResponse = new TagsResponse();
        Set<TagResponse> tags = new CopyOnWriteArraySet<>();
        Iterable<Tag> tagIterable = tagRepository.findAll();
        for (Tag tag : tagIterable) {
            double postsForTagCount = tag.getPosts().size();
            TagResponse tagResponse = new TagResponse();
            tagResponse.setName(tag.getName());
            // begin weight calculating
            double weight = postsForTagCount / postsCount;
            weight = Math.round((weight * 100)) / (double) 100; // round to .00
            // end
            tagResponse.setWeight(weight);
            tags.add(tagResponse);
        }
        tagsResponse.setTags(tags);
        return tagsResponse;
    }

    private int getTagsCount() {
        int count = 0;
        Iterable<Tag> tagIterable = tagRepository.findAll();
        for (Tag tag: tagIterable) {
            count++;
        }
        return count;
    }

    private int getPostsCount() {
        int count = 0;
        Iterable<Post> postIterable = postRepository.findAll();
        for (Post post : postIterable) {
            if (!post.isActive() || !post.getModerationStatus().equals(ModerationStatus.ACCEPTED)) {
                continue;
            }
            count++;
        }
        return count;
    }

}
