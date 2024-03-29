package com.payMyBuddy.payMyBuddy.dto;

import java.math.BigDecimal;

public record TransferDTO(String connection, String description, BigDecimal amount) {
}
