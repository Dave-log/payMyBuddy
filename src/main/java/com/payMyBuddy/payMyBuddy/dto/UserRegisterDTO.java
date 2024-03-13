package com.payMyBuddy.payMyBuddy.dto;

import java.util.Objects;

public record UserRegisterDTO(String email, String firstName, String lastName, String password) {
    public UserRegisterDTO {
        Objects.requireNonNull(email, "Email must not be null");
        Objects.requireNonNull(firstName, "First name must not be null");
        Objects.requireNonNull(lastName, "Last name must not be null");
        Objects.requireNonNull(password, "Password must not be null");
    }
}
