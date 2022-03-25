package main.api.response;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TagResponse {

    private ArrayList<TagResponseModel> tags = new ArrayList<>();

    @Data
    public static class TagResponseModel {
        private String name;
        private double weight;
    }
}
