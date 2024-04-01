package com.payMyBuddy.payMyBuddy.dto;

public record ProfileUpdateDTO(
//        String firstName,
//        String lastName,
//        String email,
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}
