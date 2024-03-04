package com.payMyBuddy.payMyBuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = { "user"})
@ToString(exclude = { "user"})
@Entity @Table(name="bank_account")
//@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_type")
    private String accountType;

    private String iban;
    private String bic;

    @Column(name = "bank_name")
    private String bankName;
}
