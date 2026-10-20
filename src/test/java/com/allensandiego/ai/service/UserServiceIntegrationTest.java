package com.allensandiego.ai.service;

import com.allensandiego.ai.dto.RegistrationRequest;
import com.allensandiego.ai.model.User;
import com.allensandiego.ai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceIntegrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser1");
        testUser.setEmail("test1@example.com");
        testUser.setPassword("{bcrypt}$2a$10$hashedPassword");
        testUser.setId(1L);
    }

    @Test
    void register_newUser_createsAccountWithEncodedPassword() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");

        when(userRepository.existsByUsername(eq("newuser"))).thenReturn(false);
        when(userRepository.existsByEmail(eq("new@example.com"))).thenReturn(false);
        when(passwordEncoder.encode(eq("password123"))).thenReturn("$2a$10$fakeHash");
        
        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        savedUser.setPassword("$2a$10$fakeHash");
        savedUser.setId(1L);
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(request);

        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getPassword()).isNotEqualTo("password123");
    }

    @Test
    void register_duplicateUsername_throwsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser1");
        request.setEmail("another@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");

        when(userRepository.existsByUsername(eq("testuser1"))).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already in use");
    }

    @Test
    void register_duplicateEmail_throwsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("anotheruser");
        request.setEmail("test1@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");

        when(userRepository.existsByUsername(eq("anotheruser"))).thenReturn(false);
        when(userRepository.existsByEmail(eq("test1@example.com"))).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already in use");
    }

    @Test
    void register_mismatchedPasswordConfirmation_throwsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("wrongpassword");

        when(userRepository.existsByUsername(eq("newuser"))).thenReturn(false);
        when(userRepository.existsByEmail(eq("new@example.com"))).thenReturn(false);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password confirmation does not match");
    }

    @Test
    void loadUserByUsername_findsExistingUser() {
        when(userRepository.findByUsernameOrEmail(eq("testuser1"))).thenReturn(Optional.of(testUser));

        var userDetails = userService.loadUserByUsername("testuser1");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser1");
    }

    @Test
    void loadUserByUsername_nonexistentUser_throwsException() {
        when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("nonexistent"))
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class);
    }
}
