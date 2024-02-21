package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="firstname")
    private String firstName;

    @Column(name="lastname")
    private String lastName;

    private Date birthdate;
    private String address;
    private String phone;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private UserAccount userAccount;

    //@ManyToMany
    //@JoinColumn(name = "id")
    //private List<User> users;
}
