package com.payMyBuddy.payMyBuddy.model;

import com.payMyBuddy.payMyBuddy.enums.RoleType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int user_id;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType role;
}
