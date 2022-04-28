package main.service;

import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import main.model.repository.PostRepository;
import main.model.repository.TagRepository;
import main.api.response.TagResponse;
import main.api.response.TagsResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
        TagsResponse tagsResponse = new TagsResponse();

        // collect tags (name, weight) to map and calculate ratio
        double ratio;
        int postsCount = getPostsCount();
        Map<String, Double> tagsWeight = new HashMap<>();
        double popularTagRawWeight = 0;
        Iterable<Tag> tagIterable = tagRepository.findAll();
        for (Tag tag : tagIterable) {
            int postsToTagsCount = tag.getPosts().size();
            double rawWeight = calculateRawWeight(postsToTagsCount, postsCount);
            if (rawWeight > popularTagRawWeight) {
                popularTagRawWeight = rawWeight;
            }
            tagsWeight.put(tag.getName(), rawWeight);
        }
        if (popularTagRawWeight == 0) {
            return tagsResponse;
        }
        ratio = 1 / popularTagRawWeight;

        Set<TagResponse> tags = new CopyOnWriteArraySet<>();
        for (Map.Entry<String, Double> entry : tagsWeight.entrySet()) {
            TagResponse tagResponse = new TagResponse();
            tagResponse.setName(entry.getKey());
            tagResponse.setWeight(entry.getValue() * ratio);
            tags.add(tagResponse);
        }
        tagsResponse.setTags(tags);
        return tagsResponse;
    }

    private double calculateRawWeight(int postsToTagCount, int postsCount) {
        double weight = (double) postsToTagCount / postsCount;
        weight = Math.round((weight * 100)) / (double) 100; // round to .00
        return weight;
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
