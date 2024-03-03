package com.payMyBuddy.payMyBuddy.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Recipient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
