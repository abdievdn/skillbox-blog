package main.service;

import main.api.response.TagResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class TagService {

    double postsCount = 50; //for weight testing

    public TagResponse getTags() {
        TagResponse tagResponse = new TagResponse();
        ArrayList<TagResponse.TagResponseModel> tagResponseModels = tagResponse.getTags();
        for (int i = 0; i < 5; i++) {
            TagResponse.TagResponseModel tagResponseModel = new TagResponse.TagResponseModel();
            tagResponseModel.setName(UUID.randomUUID().toString().substring(0, 6));
            int tagCounts = (int) ((Math.random() * 50) + 1);
            tagResponseModel.setWeight(tagCounts / postsCount);
            tagResponseModels.add(tagResponseModel);
        }
        tagResponse.setTags(tagResponseModels);

        return tagResponse;
    }
}
