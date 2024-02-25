package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private String type;
    private Date date;
    private BigDecimal amount;
    private BigDecimal fee;

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
