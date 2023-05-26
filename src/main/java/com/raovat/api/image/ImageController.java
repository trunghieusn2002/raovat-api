package com.raovat.api.image;

import com.cloudinary.Cloudinary;
import com.raovat.api.image.dto.CreateImageDTO;
import com.raovat.api.image.dto.ImageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/image")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageDTO> uploadLink(@RequestBody CreateImageDTO createImageDTO) {
        return ResponseEntity.ok(imageService.uploadLink(createImageDTO));
    }

    @PostMapping(value = "/heroku/upload", consumes = "multipart/form-data")
    public ResponseEntity<ImageDTO> uploadFileHeroku(HttpServletRequest request, @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.uploadFileHeroku(request, file));
    }

    @PostMapping(value = "/local/upload", consumes = "multipart/form-data")
    public ResponseEntity<ImageDTO> uploadFileLocal(HttpServletRequest request, @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.uploadFileLocal(request, file));
    }

    @DeleteMapping("/local/{publicId}")
    public ResponseEntity<String> deleteFileLocal(@PathVariable String publicId) {
        return ResponseEntity.ok(imageService.deleteFileLocal(publicId));
    }

    @PostMapping(value = "/cloud/upload", consumes = "multipart/form-data")
    public ResponseEntity<ImageDTO> uploadFileCloud(@RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.uploadFileCloud(file));
    }
}
