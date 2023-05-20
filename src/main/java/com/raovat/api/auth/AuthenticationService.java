package com.raovat.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raovat.api.appuser.AppUser;
import com.raovat.api.appuser.AppUserRepository;
import com.raovat.api.appuser.AppUserRole;
import com.raovat.api.appuser.AppUserService;
import com.raovat.api.auth.resetpasswordtoken.ResetPasswordToken;
import com.raovat.api.auth.resetpasswordtoken.ResetPasswordTokenService;
import com.raovat.api.config.JwtService;
import com.raovat.api.config.exception.EmailAlreadyExistException;
import com.raovat.api.email.EmailSender;
import com.raovat.api.email.EmailValidator;
import com.raovat.api.token.Token;
import com.raovat.api.token.TokenRepository;
import com.raovat.api.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ResetPasswordTokenService resetPasswordTokenService;
    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final EmailSender emailSender;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = AppUser.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .appUserRole(AppUserRole.USER)
                .locked(false)
                .enabled(true)
                .build();
        boolean userExists = appUserRepository
                .findByEmail(user.getEmail())
                .isPresent();
        if (userExists) {
            throw new EmailAlreadyExistException("Email already taken");
        }
        var savedUser = appUserRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.appUserRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private void saveUserToken(AppUser appUser, String jwtToken) {
        var token = Token.builder()
                .appUser(appUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(AppUser appUser) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(appUser.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public String changePassword(HttpServletRequest  request, ChangePasswordRequest changePasswordRequest) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return null;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            AppUser appUser = this.appUserRepository.findByEmail(userEmail)
                    .orElseThrow();

            String currentPassword = changePasswordRequest.currentPassword();
            String newPassword = changePasswordRequest.newPassword();

            if (passwordEncoder.matches(currentPassword, appUser.getPassword())) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                appUser.setPassword(encodedPassword);
                appUserRepository.save(appUser);
                return "Password changed successfully.";
            } else {
                return "Incorrect current password.";
            }
        }
        return null;
    }

    public String forgetPassword(String email) {
        emailValidator.test(email);

        AppUser appUser = appUserService.findByEmail(email);

        String token = UUID.randomUUID().toString();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        resetPasswordTokenService.saveConfirmationToken(resetPasswordToken);

        String link = "http://localhost:8080/api/v1/auth/confirm?token=" + token;

        emailSender.send(
                email,
                buildEmail(appUser.getFirstName(), link)
        );

        return "Success";
    }

    @Transactional
    public AuthenticationResponse confirmToken(String token) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenService
                .getToken(token);
        if (resetPasswordToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = resetPasswordToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        AppUser appUser = resetPasswordToken.getAppUser();

        resetPasswordTokenService.setConfirmedAt(token);
        var jwtToken = jwtService.generateToken(appUser);
        var refreshToken = jwtService.generateRefreshToken(appUser);
        revokeAllUserTokens(appUser);
        saveUserToken(appUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String newPassword(String password, String token) {
        final String userEmail;
        String authHeader = token;
        if (authHeader == null) {
            return null;
        }
        //String refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(token);
        if (userEmail != null) {
            AppUser appUser = this.appUserService.findByEmail(userEmail);
            ResetPasswordToken resetPasswordToken = resetPasswordTokenService.findByEmail(userEmail);
            if (resetPasswordToken.getConfirmedAt() != null && LocalDateTime.now().minusMinutes(15).isBefore(resetPasswordToken.getConfirmedAt())) {
                appUser.setPassword(passwordEncoder.encode(password));
                appUserRepository.save(appUser);
                return "Success";
            }
            else {
                return "Failed";
            }
        }
        return "Failed";
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Please click on the below link to reset your password: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Reset Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
