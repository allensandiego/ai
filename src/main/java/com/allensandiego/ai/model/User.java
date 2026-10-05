package com.allensandiego.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * User entity representing a registered user account.
 * Maps to the users table in the database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 100)
    private String email;

    @Column(name = "enabled")
    private boolean enabled = true;

}
