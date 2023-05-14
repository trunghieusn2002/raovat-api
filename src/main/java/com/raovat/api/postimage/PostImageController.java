package com.raovat.api.postimage;

import com.raovat.api.postimage.dto.CreateImageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/image")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ImageController {

    private final ImageService imageService;

    @GetMapping
    public ResponseEntity<List<Image>> getAll() {
        return ResponseEntity.ok(imageService.getAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Image> getById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Image> create(@RequestBody CreateImageDTO createImageDTO) {
        return ResponseEntity.ok(imageService.create(createImageDTO));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Image> update(@PathVariable Long id, @RequestBody Image image) {
        return ResponseEntity.ok(imageService.update(id, image));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.delete(id));
    }

    @PostMapping(value = "/local/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFileLocal(HttpServletRequest request, @RequestPart("file") MultipartFile file) throws IOException {
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
            return ResponseEntity.ok((request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") ? "localhost" : request.getRemoteAddr()) + ":" + request.getLocalPort() + "/uploads/" + fileName);
        } else {
            return ResponseEntity.ok("Failed");
        }
    }

    @DeleteMapping("/local/{publicId}")
    public ResponseEntity<String> deleteFileLocal(@PathVariable String publicId) {
        try {
            if (publicId == null || publicId.trim().equals(""))
                throw new RuntimeException("Not found publicId");
            File file = new File(System.getProperty("user.dir"), "uploads/" + publicId);
            if (file.delete()) {
                return ResponseEntity.ok("success");
            } else {
                throw new RuntimeException("Some thing went wrong!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
