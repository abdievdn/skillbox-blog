package main.response;

import lombok.Data;

import java.util.Set;

@Data
public class TagsResponse {

    private Set<TagResponse> tags;
}
