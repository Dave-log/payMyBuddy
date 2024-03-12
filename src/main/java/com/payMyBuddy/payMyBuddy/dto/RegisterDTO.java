package com.payMyBuddy.payMyBuddy.dto;

import java.util.Objects;

public record RegisterDTO(String email, String firstName, String lastName, String password) {
    public RegisterDTO {
        Objects.requireNonNull(email);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(password);
    }
}
