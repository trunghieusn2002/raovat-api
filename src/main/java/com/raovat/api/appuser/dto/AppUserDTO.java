package com.raovat.api.appuser.dto;

import com.raovat.api.appuser.AppUserRole;
import com.raovat.api.image.dto.ImageDTO;

public record AppUserDTO(String firstName, String lastName, String email, String phone, AppUserRole appUserRole) {
}
