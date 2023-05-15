package com.raovat.api.image;

import com.raovat.api.image.dto.ImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    ImageDTO toDTO(Image image);
}
