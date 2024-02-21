package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="userAccount")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int user_id;
    private String email;
    private String password;
    private String role;
}
