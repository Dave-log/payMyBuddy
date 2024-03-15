package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "buddy_transaction")
public class BuddyTransaction extends Transaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @Override
    public long getRecipientId() {
        return recipientUser.getId();
    }
}
