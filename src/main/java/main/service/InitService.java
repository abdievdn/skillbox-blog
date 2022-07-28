package main.service;

import main.api.response.general.InitResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InitService {

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

    public InitResponse getInitInfo() {
        InitResponse initResponse = new InitResponse();
        initResponse.setTitle(title);
        initResponse.setSubtitle(subtitle);
        initResponse.setPhone(phone);
        initResponse.setEmail(email);
        initResponse.setCopyright(copyright);
        initResponse.setCopyrightFrom(copyrightFrom);
        return initResponse;
    }
}