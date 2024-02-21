package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int sender_id;
    private int recipient_id;
    private String description;
    private String type;
    private Date date;
    private BigDecimal amount;
    private BigDecimal fee;
}
