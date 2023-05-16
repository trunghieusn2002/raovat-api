package com.raovat.api.auth;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
