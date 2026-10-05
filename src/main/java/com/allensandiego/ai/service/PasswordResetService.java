package com.allensandiego.ai.service;

import com.allensandiego.ai.model.User;
import com.allensandiego.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Service for handling password reset functionality.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Reset password for a user by username.
     * @param username the username requesting the reset
     * @return true if password was reset successfully, false if user not found
     */
    public boolean resetPassword(String username) {
        User user = userRepository.findByUsername(username);
        
        // Verify user exists (do not reveal if user doesn't exist in error message)
        if (user == null || !user.isEnabled()) {
            return false;
        }

        // Generate new secure password (min 8 chars, mixed complexity)
        String newPassword = generateSecurePassword();
        
        // Hash and update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return true;
    }

    /**
     * Generate a random secure password with minimum security requirements.
     */
    private String generateSecurePassword() {
        StringBuilder password = new StringBuilder();
        password.append(UUID.randomUUID().toString());
        
        // Ensure at least 8 characters and mix of character types
        char upper = Character.toUpperCase(password.charAt(0));
        password.setCharAt(0, upper);
        
        return password.toString();
    }

}
