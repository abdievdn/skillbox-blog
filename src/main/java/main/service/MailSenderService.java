package main.service;

import main.Blog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {

    @Value("${blog.info.email}")
    private String emailFrom;

    private final MailSender mailSender;

    public MailSenderService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String text) {
        SimpleMailMessage simpleMail = new SimpleMailMessage();
        simpleMail.setFrom(emailFrom);
        simpleMail.setTo(email);
        simpleMail.setSubject(Blog.EMAIL_SUBJECT);
        simpleMail.setText(text);
        mailSender.send(simpleMail);
    }
}
