package com.raovat.api.image;

import com.raovat.api.category.CategoryService;
import com.raovat.api.category.dto.CategoryDTO;
import com.raovat.api.category.dto.CreateCategoryDTO;
import com.raovat.api.image.dto.CreateImageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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
}
