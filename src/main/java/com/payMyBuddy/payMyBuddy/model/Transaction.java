package com.payMyBuddy.payMyBuddy.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.payMyBuddy.payMyBuddy.enums.TransactionStatus;
import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@DynamicUpdate
@Entity @Table(name="transaction")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Transaction {

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

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

//    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
//    @JsonIdentityReference(alwaysAsId=true)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "recipient_user_id")
//    private User recipientUser;
//
//    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
//    @JsonIdentityReference(alwaysAsId=true)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "recipient_bank_id")
//    private BankAccount recipientBank;
}
