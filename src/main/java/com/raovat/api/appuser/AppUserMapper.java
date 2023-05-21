package com.raovat.api.appuser;

import com.raovat.api.appuser.dto.AppUserDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    AppUserDTO toDTO(AppUser appUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "email", ignore = true)
    void updateEntity(@MappingTarget AppUser appUser, AppUserDTO appUserDTO);
}
