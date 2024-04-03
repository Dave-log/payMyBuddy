package com.payMyBuddy.payMyBuddy.dto;

import java.util.Objects;

public record BankAccountRegisterDTO(String accountType, String accountNumber, String iban, String bic, String bankName) {}
