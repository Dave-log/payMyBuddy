package com.payMyBuddy.payMyBuddy.dto;

import com.payMyBuddy.payMyBuddy.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Objects;

public record BankTransactionRequestDTO(String description, BigDecimal amount, TransactionType type, boolean feePaidBySender) {
    public BankTransactionRequestDTO {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(type, "Transaction type must not be null");
    }
}