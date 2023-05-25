package com.raovat.api.appuser;

import com.raovat.api.appuser.dto.AppUserDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/app-user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<AppUserDTO> get(HttpServletRequest request) {
        return ResponseEntity.ok(appUserService.get(request));
    }

    @GetMapping("/{email}")
    public ResponseEntity<AppUserDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(appUserService.getByEmail(email));
    }

    @PatchMapping()
    public ResponseEntity<AppUserDTO> update(HttpServletRequest request, AppUserDTO appUserDTO) {
        return ResponseEntity.ok(appUserService.update(request, appUserDTO));
    }
}
