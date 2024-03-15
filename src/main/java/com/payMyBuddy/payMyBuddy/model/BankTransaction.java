package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bank_transaction")
public class BankTransaction extends Transaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_bank_id")
    private BankAccount recipientBank;

    @Override
    public long getRecipientId() {
        return recipientBank.getId();
    }
}
