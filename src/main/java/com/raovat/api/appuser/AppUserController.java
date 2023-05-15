package com.raovat.api.appuser;

import com.raovat.api.appuser.dto.AppUserDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/app-user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/{email}")
    public ResponseEntity<AppUserDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(appUserService.getByEmail(email));
    }

    @PatchMapping("/{email}")
    public ResponseEntity<AppUserDTO> update(@PathVariable String email, AppUserDTO appUserDTO) {
        return ResponseEntity.ok(appUserService.update(email, appUserDTO));
    }
}
