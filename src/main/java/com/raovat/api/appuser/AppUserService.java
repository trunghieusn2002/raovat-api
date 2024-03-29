package com.raovat.api.appuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raovat.api.appuser.dto.AppUserDTO;
import com.raovat.api.auth.AuthenticationResponse;
import com.raovat.api.auth.AuthenticationService;
import com.raovat.api.config.JwtService;
import com.raovat.api.auth.resetpasswordtoken.ResetPasswordToken;
import com.raovat.api.auth.resetpasswordtoken.ResetPasswordTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND = "User with email %s not found";
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetPasswordTokenService resetPasswordTokenService;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();
        if (userExists) {
            throw new IllegalStateException("Email already taken");
        }
        String encodedPassword = passwordEncoder
                .encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        resetPasswordTokenService.saveConfirmationToken(resetPasswordToken);
        return token;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    public AppUserDTO getByEmail(String email) {
        return AppUserMapper.INSTANCE.toDTO(findByEmail(email));
    }

    public AppUserDTO update(HttpServletRequest request, AppUserDTO appUserDTO) {
        AppUser appUser = getCurrentUser(request);
        AppUserMapper.INSTANCE.updateEntity(appUser, appUserDTO);
        return AppUserMapper.INSTANCE.toDTO(
                appUserRepository.save(appUser)
        );
    }

    public AppUserDTO get(HttpServletRequest request) {
        return AppUserMapper.INSTANCE.toDTO(getCurrentUser(request));
    }

    public AppUser getCurrentUser(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token = jwtService.extractToken(authHeader);
        final String email = jwtService.extractUsername(token);
        if (email == null) {
            throw new IllegalStateException("Email is null");
        }
        return findByEmail(email);
    }
}
