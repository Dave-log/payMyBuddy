package com.payMyBuddy.payMyBuddy.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bank_transaction")
public class BankTransaction extends Transaction {

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_bank_id")
    private BankAccount recipientBank;

    @Override
    public long getRecipientId() {
        return recipientBank.getId();
    }
}
