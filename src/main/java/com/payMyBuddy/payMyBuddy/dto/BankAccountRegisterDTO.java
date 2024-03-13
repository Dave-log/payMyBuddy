package com.payMyBuddy.payMyBuddy.dto;

import java.util.Objects;

public record BankAccountRegisterDTO(String accountNumber, String accountType, String iban, String bic, String bankName) {
    public BankAccountRegisterDTO{
        Objects.requireNonNull(accountNumber, "Account number must not be null");
        Objects.requireNonNull(iban, "IBAN must not be null");
        Objects.requireNonNull(bic, "BIC must not be null");
    }
}
