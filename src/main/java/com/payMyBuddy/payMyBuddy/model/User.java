package com.payMyBuddy.payMyBuddy.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.payMyBuddy.payMyBuddy.enums.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "bankAccounts"})
@ToString(exclude = { "bankAccounts"})
@DynamicUpdate
@Entity @Table(name = "user", uniqueConstraints = {@UniqueConstraint(name = "unique email", columnNames = {"email"})})
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class User extends Recipient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @OneToMany(targetEntity = BankAccount.class, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BankAccount> bankAccounts = new ArrayList<>();

    @OneToMany(targetEntity = Transaction.class, mappedBy = "sender")
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "buddy_id") )
    private Set<User> buddies = new HashSet<>();

    //private Date birthdate;
    //private String address;
    //private String phone;
}
