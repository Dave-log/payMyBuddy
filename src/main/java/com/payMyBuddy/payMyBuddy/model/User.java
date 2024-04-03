package com.payMyBuddy.payMyBuddy.model;

import com.fasterxml.jackson.annotation.*;
import com.payMyBuddy.payMyBuddy.enums.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.util.*;

@Data
@DynamicUpdate
@Entity
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(name = "unique email", columnNames = {"email"})})
//@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
@ToString(exclude = "buddies")
@EqualsAndHashCode(exclude = "buddies")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="firstname")
    private String firstName;

    @Column(name="lastname")
    private String lastName;

    private String email;
    private String password;
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private RoleType role = RoleType.USER;

    //@JsonIgnoreProperties({"buddies", "role", "password", "id"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "buddy_id"))
    private Set<User> buddies = new HashSet<>();
}
