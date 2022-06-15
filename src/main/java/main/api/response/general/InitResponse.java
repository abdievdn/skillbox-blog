package main.api.response.general;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Data
public class InitResponse {

    @Value("${blog.info.title}")
    private String title;

    @Value("${blog.info.subtitle}")
    private String subtitle;

    @Value("${blog.info.phone}")
    private String phone;

    @Value("${blog.info.email}")
    private String email;

    @Value("${blog.info.copyright}")
    private String copyright;

    @Value("${blog.info.copyrightFrom}")
    private String copyrightFrom;

    @Data
    public static class TagsResponse {

        private Set<TagResponse> tags;
    }

    @Data
    public static class TagResponse {
        private String name;
        private double weight;
    }
}
