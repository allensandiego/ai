package com.allensandiego.ai.service;

import com.allensandiego.ai.model.User;
import com.allensandiego.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling user registration.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user.
     * @param username the username to register
     * @param email optional email address
     * @param password the plaintext password
     * @return true if registration successful, false if username already exists
     */
    public boolean registerUser(String username, String email, String password) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            return false;
        }

        // Validate password length
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        // Validate username format
        if (username == null || username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be 3-50 alphanumeric characters");
        }

        // Create and save user with hashed password
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEnabled(true);

        userRepository.save(user);
        return true;
    }

}
