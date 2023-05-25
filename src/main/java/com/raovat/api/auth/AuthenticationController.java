package com.raovat.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/forget-password")
    public String forgetPassword(@RequestParam(value = "email") String email) {
        return authenticationService.forgetPassword(email);
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<AuthenticationResponse> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(authenticationService.confirmToken(token));
    }

    @PostMapping(path = "new-password")
    public ResponseEntity<String> newPassword(@RequestParam String password, @RequestParam String token) {
        return ResponseEntity.ok(authenticationService.newPassword(password, token));
    }

}
