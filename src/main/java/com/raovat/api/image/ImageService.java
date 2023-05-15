package com.raovat.api.image;

import com.raovat.api.image.dto.CreateImageDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public String uploadFileHeroku(HttpServletRequest request, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
        String fileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        // Kiểm tra nếu file là ảnh
        boolean isImage = file.getContentType().startsWith("image/");
        if (isImage) {
            // Đọc dữ liệu của file vào một mảng byte
            byte[] fileData = file.getBytes();
            // Tạo một đối tượng BufferedImage từ dữ liệu của file
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileData));
            // Thiết lập kích thước mới cho ảnh (full HD)
            int newWidth = 1920;
            int newHeight = 1080;
            // Kiểm tra kích thước của ảnh
            boolean isLargeImage = originalImage != null && originalImage.getWidth() > newWidth && originalImage.getHeight() > newHeight;
            // Nếu ảnh lớn hơn kích thước cho phép, resize ảnh
            if (isLargeImage) {
                // Tạo một đối tượng BufferedImage mới với kích thước mới và vẽ ảnh gốc lên đó
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();

                // Chuyển đổi ảnh mới thành mảng byte
                ByteArrayOutputStream newImageBytes = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", newImageBytes);
                fileData = newImageBytes.toByteArray();
            }
            // Upload file lên Cloudinary
            // Trả về URL của file đã upload

            Path filePath = Paths.get(uploadPath.toString(), fileName);
            Files.write(filePath, fileData);
            String url = "rao-vat-api.herokuapp.com" + "/uploads/" + fileName;
            imageRepository.save(new Image(fileName, url));
            return url;
        } else {
            return "Failed";
        }

    }

    public String uploadFileLocal(HttpServletRequest request, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
        String fileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        // Kiểm tra nếu file là ảnh
        boolean isImage = file.getContentType().startsWith("image/");
        if (isImage) {
            // Đọc dữ liệu của file vào một mảng byte
            byte[] fileData = file.getBytes();
            // Tạo một đối tượng BufferedImage từ dữ liệu của file
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileData));
            // Thiết lập kích thước mới cho ảnh (full HD)
            int newWidth = 1920;
            int newHeight = 1080;
            // Kiểm tra kích thước của ảnh
            boolean isLargeImage = originalImage != null && originalImage.getWidth() > newWidth && originalImage.getHeight() > newHeight;
            // Nếu ảnh lớn hơn kích thước cho phép, resize ảnh
            if (isLargeImage) {
                // Tạo một đối tượng BufferedImage mới với kích thước mới và vẽ ảnh gốc lên đó
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();

                // Chuyển đổi ảnh mới thành mảng byte
                ByteArrayOutputStream newImageBytes = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", newImageBytes);
                fileData = newImageBytes.toByteArray();
            }
            // Upload file lên Cloudinary
            // Trả về URL của file đã upload

            Path filePath = Paths.get(uploadPath.toString(), fileName);
            Files.write(filePath, fileData);
            String url = (request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") ? "localhost" : request.getRemoteAddr()) + ":" + request.getLocalPort() + "/uploads/" + fileName;
            imageRepository.save(new Image(fileName, url));
            return url;
        } else {
            return "Failed";
        }

    }

    public String deleteFileLocal(String publicId) {
        try {
            if (publicId == null || publicId.trim().equals(""))
                throw new RuntimeException("Not found publicId");
            File file = new File(System.getProperty("user.dir"), "uploads/" + publicId);
            if (file.delete()) {
                imageRepository.deleteByName(publicId);
                return "success";
            } else {
                throw new RuntimeException("Some thing went wrong!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String uploadLink(CreateImageDTO createImageDTO) {
        imageRepository.save(new Image(createImageDTO.name(), createImageDTO.url()));
        return "Success";
    }
}
