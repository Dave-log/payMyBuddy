package com.payMyBuddy.payMyBuddy.dto;

public record PasswordUpdateDTO(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}
