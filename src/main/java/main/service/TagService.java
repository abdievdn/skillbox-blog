package main.service;

import lombok.AllArgsConstructor;
import main.api.response.general.TagResponse;
import main.api.response.general.TagsResponse;
import main.model.Post;
import main.model.Tag;
import main.model.repository.PostRepository;
import main.model.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public TagsResponse getTags() {
        TagsResponse tagsResponse = new TagsResponse();

// collect tags (name, weight) to map and calculate ratio
        double ratio;
        int postsCount = getPostsCount();
        Map<String, Double> tagsWeight = new HashMap<>();
        double popularTagRawWeight = 0;
        Iterable<Tag> tagIterable = tagRepository.findAll();
        for (Tag tag : tagIterable) {
            Set<Post> tagPosts = tag.getPosts();
            int postsToTagCount = 0;
    // check actual posts for tag
            for (Post postInTag : tagPosts) {
                if (PostService.isNotActual(postInTag)) {
                    continue;
                }
                postsToTagCount++;
            }
            double rawWeight = calculateRawWeight(postsToTagCount, postsCount);
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
            if (PostService.isNotActual(post)) {
                continue;
            }
            count++;
        }
        return count;
    }

    public Tag getTagByName(String name) {
        return tagRepository.findByName(name).isPresent() ? tagRepository.findByName(name).get() : null;
    }
}
