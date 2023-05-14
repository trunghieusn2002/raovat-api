package com.raovat.api.image;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/image")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/local/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFileLocal(HttpServletRequest request, @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.uploadFileLocal(request, file));
    }

    @DeleteMapping("/local/{publicId}")
    public ResponseEntity<String> deleteFileLocal(@PathVariable String publicId) {
        return ResponseEntity.ok(imageService.deleteFileLocal(publicId));
    }
}
