package main.service.util;

import main.controller.advice.error.ProfileMyError;
import main.controller.advice.exception.ImageUploadException;
import main.controller.advice.exception.ProfileMyException;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    public final static String DEFAULT_PATH = "src\\main\\resources\\static\\img\\";

    public static void save(String uploadDir, String fileName, String formatName, MultipartFile file, int width, int height) throws IOException {
        if (!file.getContentType().equals("image/jpeg"))
            if (!file.getContentType().equals("image/png")) {
                throw new IOException();
            }
        BufferedImage photoInput = ImageIO.read(file.getInputStream());
        if (width == 0 || height == 0) {
            width = photoInput.getWidth();
            height = photoInput.getHeight();
        }
        BufferedImage photoOutput = new BufferedImage(width, height, photoInput.getType());
        Graphics2D image = photoOutput.createGraphics();
        image.drawImage(photoInput, 0, 0, width, height, null);
        image.dispose();
        File path = new File(DEFAULT_PATH + uploadDir);
        if (!path.exists()) path.mkdirs();
        ImageIO.write(photoOutput, formatName, new File(path, fileName + '.' + formatName));
    }

    public static String getFormatName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
    }
}