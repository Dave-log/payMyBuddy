package com.payMyBuddy.payMyBuddy.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record BuddyTransactionRequestDTO(String description, String recipientEmail, BigDecimal amount, boolean feePaidBySender) {
    public BuddyTransactionRequestDTO {
        Objects.requireNonNull(recipientEmail, "Recipient email must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
    }
}
