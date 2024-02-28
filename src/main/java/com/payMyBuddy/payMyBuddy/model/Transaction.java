package com.payMyBuddy.payMyBuddy.model;

import com.payMyBuddy.payMyBuddy.enums.TransactionStatus;
import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String description;
    private Date date;
    private BigDecimal amount;
    private BigDecimal fee;

    @Column(name = "fee_paid_by_sender")
    private boolean feePaidBySender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Any()
    @AnyDiscriminator(DiscriminatorType.STRING)
    @AnyKeyJavaClass(Integer.class)
    @AnyDiscriminatorValue(discriminator = "USER", entity = User.class)
    @AnyDiscriminatorValue(discriminator = "BANK_ACCOUNT", entity = BankAccount.class)
    @JoinColumn(name = "recipient_id")
    @Column(name = "recipient_type")
    private Object recipient;
}
