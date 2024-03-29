package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.AllArgsConstructor;
import main.Blog;
import main.model.CaptchaCode;
import main.model.repository.CaptchaCodeRepository;
import main.api.response.auth.CaptchaCodeResponse;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CaptchaCodeService {

    private final CaptchaCodeRepository captchaCodeRepository;

    public CaptchaCodeResponse generateCaptcha() throws IOException {
        deleteExpiredCaptcha();
        CaptchaCodeResponse captchaResponse = new CaptchaCodeResponse();
        String secret = UUID.randomUUID().toString().substring(0, 8);
        Cage cage = new GCage();
        String code = cage.getTokenGenerator().next().substring(0, 4);
        byte[] image;
        InputStream inputStream = new ByteArrayInputStream(cage.draw(code));
        image = inputStream.readAllBytes();
        String codeImage = "data:image/png;base64, " + Base64.getEncoder().encodeToString(image);
        captchaResponse.setSecret(secret);
        captchaResponse.setImage(codeImage);
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setSecretCode(secret);
        captchaCode.setCode(code);
        captchaCode.setTime(LocalDateTime.now());
        captchaCodeRepository.save(captchaCode);
        return captchaResponse;
    }

    private void deleteExpiredCaptcha() {
        LocalDateTime now = LocalDateTime.now();
        Iterable<CaptchaCode> captchaCodeIterable = captchaCodeRepository.findAll();
        for (CaptchaCode captcha : captchaCodeIterable) {
            Duration duration = Duration.between(captcha.getTime(), now);
            if (duration.toMinutes() >= Blog.CAPTCHA_TIME_EXPIRED_MIN) {
                captchaCodeRepository.deleteById(captcha.getId());
            }
        }
    }

    public boolean checkCaptchaCode(String captchaCode, String captchaSecretCode) {
        Optional<CaptchaCode> captcha = captchaCodeRepository.findByCode(captchaCode);
        return captcha.isPresent() && captcha.get().getSecretCode().equals(captchaSecretCode);
    }
}