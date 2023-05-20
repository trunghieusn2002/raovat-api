package com.raovat.api.auth.resetpasswordtoken;

import com.raovat.api.config.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ResetPasswordTokenService {

    private static final String TOKEN_NOT_FOUND = "Token %s not found";
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    public void saveConfirmationToken(ResetPasswordToken token) {
        resetPasswordTokenRepository.save(token);
    }

    public ResetPasswordToken getToken(String token) {
        return resetPasswordTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new IllegalStateException(String.format(TOKEN_NOT_FOUND, token)));
    }

    public ResetPasswordToken findByEmail(String email) {
        return resetPasswordTokenRepository.findByAppUserEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No token found with email " + email));
    }

    public int setConfirmedAt(String token) {
        return resetPasswordTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
