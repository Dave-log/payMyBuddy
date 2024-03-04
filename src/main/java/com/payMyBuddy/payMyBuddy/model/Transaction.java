package com.payMyBuddy.payMyBuddy.model;

import com.payMyBuddy.payMyBuddy.enums.TransactionStatus;
import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(exclude = {"sender", "recipientUser", "recipientBank"})
@ToString(exclude = {"sender", "recipientUser", "recipientBank"})
@DynamicUpdate
@Entity @Table(name="transaction")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String description;

    @Temporal(TemporalType.DATE)
    private Date date;

    private BigDecimal amount;
    private BigDecimal fee;

    @Column(name = "fee_payer")
    private boolean feePaidBySender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_bank_id")
    private BankAccount recipientBank;
}
