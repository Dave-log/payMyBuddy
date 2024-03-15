package com.payMyBuddy.payMyBuddy.dto;

import com.payMyBuddy.payMyBuddy.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Objects;

public record BankTransactionRequestDTO(long id, String description, TransactionType type, BigDecimal amount, boolean feePaidBySender) {
    public BankTransactionRequestDTO {
        Objects.requireNonNull(type, "Transaction type must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
    }
}