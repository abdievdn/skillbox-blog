package main.service;

import main.Blog;
import main.controller.advice.error.ImageUploadError;
import main.controller.advice.exception.ImageUploadException;
import main.service.utils.ImageUtil;
import main.service.utils.TimestampUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageService {
    public String uploadImage(MultipartFile image) throws ImageUploadException, IOException {
        if (image != null) {
            if (image.getBytes().length > Blog.IMAGE_LIMIT_WEIGHT) {
                throw new ImageUploadException(ImageUploadError.IMAGE);
            }
            String uploadDir = Blog.PATH_FOR_UPLOAD;
            String randomDirs = UUID.randomUUID().toString();
            uploadDir +=
                    randomDirs.substring(0, 2) + "/" +
                    randomDirs.substring(2, 4) + "/" +
                    randomDirs.substring(4, 6) + "/";
            String fileName = TimestampUtil.encode(LocalDateTime.now()) + randomDirs.substring(6, 12);
            String fileFormat = ImageUtil.getFormatName(image);
            ImageUtil.saveImage(uploadDir, fileName, fileFormat, image, 0, 0);
            return uploadDir + fileName + '.' + fileFormat;
        } else {
            return null;
        }
    }
}
