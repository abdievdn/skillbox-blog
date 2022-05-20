package main.service;

import main.controller.advice.exception.ProfileMyException;
import main.service.util.ImageUtil;
import main.service.util.TimestampUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageService {

    public String uploadImage(MultipartFile image) throws ProfileMyException, IOException {
        String uploadDir = "\\upload";
        String randomDirs = UUID.randomUUID().toString();
        uploadDir += "\\" +
                randomDirs.substring(0, 2) + "\\" +
                randomDirs.substring(2, 4) + "\\" +
                randomDirs.substring(4, 6) + "\\";
        String fileName = String.valueOf(TimestampUtil.encode(LocalDateTime.now()));
        String fileFormat = ImageUtil.getFormatName(image);
        ImageUtil.save(uploadDir, fileName, fileFormat, image, 0, 0);
        return uploadDir.replace('\\', '/') + fileName + '.' + fileFormat;
    }
}
