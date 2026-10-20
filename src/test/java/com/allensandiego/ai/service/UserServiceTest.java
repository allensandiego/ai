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
class UserServiceTest {

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
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedHash");
        testUser.setId(1L);
    }

    @Test
    void register_validInput_createsUserWithEncodedPassword() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");

        when(userRepository.existsByUsername(eq("newuser"))).thenReturn(false);
        when(userRepository.existsByEmail(eq("newuser@example.com"))).thenReturn(false);
        when(passwordEncoder.encode(eq("password123"))).thenReturn("$2a$10$encodedHash");
        
        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com");
        savedUser.setPassword("$2a$10$encodedHash");
        savedUser.setId(1L);
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(request);

        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("newuser@example.com");
        assertThat(result.getPassword()).isEqualTo("$2a$10$encodedHash");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("existinguser");
        request.setEmail("newemail@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");

        when(userRepository.existsByUsername(eq("existinguser"))).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already in use");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("existingemail@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(eq("existingemail@example.com"))).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already in use");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_mismatchedPasswordConfirmation_throwsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("newemail@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("wrongpassword");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password confirmation does not match");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loadUserByUsername_foundUser_returnsUserDetails() {
        when(userRepository.findByUsernameOrEmail(eq("testuser"))).thenReturn(Optional.of(testUser));

        var result = userService.loadUserByUsername("testuser");

        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedHash");
    }

    @Test
    void loadUserByUsername_notFound_throwsException() {
        when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("nonexistent"))
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
                .hasMessageContaining("No user found");
    }
}
