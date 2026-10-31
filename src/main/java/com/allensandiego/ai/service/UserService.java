package com.allensandiego.ai.service;

import com.allensandiego.ai.domain.Role;
import com.allensandiego.ai.domain.User;
import com.allensandiego.ai.repository.RoleRepository;
import com.allensandiego.ai.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for handling user registration and authentication operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with the provided credentials and roles.
     *
     * @param username  The username for the account (3-30 characters)
     * @param password  The password (min 8 chars, uppercase, lowercase, digit, special char)
     * @param roleCodes Comma-separated list of role codes to assign to the user
     * @return The created User entity
     * @throws ConstraintViolationException if validation fails
     */
    @Transactional
    public User register(String username, String password, List<String> roleCodes) {
        // Validate username
        validateUsername(username);

        // Check if user already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User with username '" + username + "' already exists");
        }

        // Hash the password using BCrypt
        String encodedPassword = passwordEncoder.encode(password);

        // Create User entity
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEnabled(true);

        // Fetch and assign roles
        List<Role> roles = fetchRoles(roleCodes);
        if (!roles.isEmpty()) {
            String authorities = roles.stream()
                    .map(Role::getCode)
                    .collect(Collectors.joining(","));
            user.setAuthorities(authorities);
        }

        // Save the user
        return userRepository.save(user);
    }

    /**
     * Loads a User by username.
     *
     * @param username The username to search for
     * @return Optional containing the User if found, empty otherwise
     */
    public Optional<User> loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Loads a User by username or throws exception (for Spring Security).
     *
     * @param username The username to search for
     * @return The User if found
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if not found
     */
    public User loadUserByUsernameOrThrow(String username) {
        return loadUserByUsername(username)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "User with username '" + username + "' not found"
                ));
    }

    /**
     * Validates the username according to business rules.
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        String trimmed = username.trim();
        if (trimmed.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        if (trimmed.length() > 30) {
            throw new IllegalArgumentException("Username cannot exceed 30 characters");
        }

        // Additional validation: username should only contain alphanumeric characters and underscores
        if (!trimmed.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }
    }

    /**
     * Validates the password according to business rules.
     */
    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        String trimmed = password.trim();
        if (trimmed.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        boolean hasUpperCase = trimmed.matches(".*[A-Z].*");
        boolean hasLowerCase = trimmed.matches(".*[a-z].*");
        boolean hasDigit = trimmed.matches(".*\\d.*");
        boolean hasSpecialChar = trimmed.matches(".*[^a-zA-Z0-9].*");

        if (!hasUpperCase) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!hasLowerCase) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!hasDigit) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        if (!hasSpecialChar) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

    /**
     * Fetches roles by their codes from the database.
     */
    private List<Role> fetchRoles(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return List.of();
        }

        // Normalize role codes (trim and convert to uppercase for comparison)
        List<String> normalizedCodes = roleCodes.stream()
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toList());

        if (normalizedCodes.isEmpty()) {
            return List.of();
        }

        // Fetch all enabled roles and filter by code
        return roleRepository.findAll().stream()
                .filter(role -> role.isEnabled())
                .filter(role -> normalizedCodes.contains(role.getCode()))
                .collect(Collectors.toList());
    }

    /**
     * Registers a user using the RegistrationRequest DTO.
     */
    @Transactional
    public User register(RegistrationRequest request) {
        validateUsername(request.getUsername());
        validatePassword(request.getPassword());

        // Parse role codes from comma-separated string
        List<String> roleCodes = Arrays.stream(request.getRoles().split(","))
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toList());

        return register(request.getUsername(), request.getPassword(), roleCodes);
    }
}
