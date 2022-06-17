package main.api.response.general;

import lombok.Data;
import main.api.response.BlogResponse;

import java.util.Set;

@Data
public class TagsResponse implements BlogResponse {
    private Set<TagResponse> tags;
}