package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="bankAccount")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int user_id;
    private String accountNumber;
    private String accountType;
    private String iban;
    private String bic;
    private String bankName;
}
