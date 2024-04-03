package com.payMyBuddy.payMyBuddy.dto;

import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankTransactionRequestDTO {
    private long id;
    private String description;
    private String type;
    private BigDecimal amount;
    private boolean feePaidBySender;
}
