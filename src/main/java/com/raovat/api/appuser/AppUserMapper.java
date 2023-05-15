package com.raovat.api.appuser;

import com.raovat.api.appuser.dto.AppUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    AppUserDTO toDTO(AppUser appUser);
    void updateEntity(@MappingTarget AppUser appUser, AppUserDTO appUserDTO);
}
