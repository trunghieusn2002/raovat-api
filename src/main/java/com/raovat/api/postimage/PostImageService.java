package com.raovat.api.postimage;

import com.raovat.api.postimage.dto.CreateImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public List<Image> getAll() {
        return imageRepository.findAll();
    }

    public Image getById(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id"));
    }

    public Image create(CreateImageDTO createImageDTO) {
        return imageRepository.save(new Image(createImageDTO.url()));
    }

    public Image update(Long id, Image imageUpdate) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found id"));
        image.setUrl(imageUpdate.getUrl());
        return imageRepository.save(image);
    }

    public String delete(Long id) {
        imageRepository.deleteById(id);
        return "Success";
    }
}
