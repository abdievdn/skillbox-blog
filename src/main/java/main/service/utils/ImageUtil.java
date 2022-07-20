package main.service.utils;

import main.Blog;
import main.controller.DefaultController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ImageUtil {

    public static void saveImage(String uploadDir, String fileName, String formatName, MultipartFile file, int width, int height) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).equals("image/jpeg"))
            if (!file.getContentType().equals("image/png")) {
                throw new IOException();
            }
        BufferedImage photoInput = ImageIO.read(file.getInputStream());
        // if width and height is zero then check image size, if it is larger than max column width then do resize
        if (width == 0 || height == 0) {
            if (photoInput.getWidth() > Blog.IMAGE_MAX_WIDTH) {
                width = Blog.IMAGE_MAX_WIDTH;
                height = photoInput.getHeight() * (Blog.IMAGE_MAX_WIDTH * 100 / photoInput.getWidth()) / 100;
            } else {
                width = photoInput.getWidth();
                height = photoInput.getHeight();
            }
        }
        BufferedImage photoOutput = new BufferedImage(width, height, photoInput.getType());
        Graphics2D image = photoOutput.createGraphics();
        image.drawImage(photoInput, 0, 0, width, height, null);
        image.dispose();
        File path = new File(DefaultController.getDefaultPath() + uploadDir);
        if (!path.exists() && !path.mkdirs()) return;
        ImageIO.write(photoOutput, formatName, new File(path, fileName + '.' + formatName));
    }

    public static String getFormatName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf('.') + 1);
    }
}
