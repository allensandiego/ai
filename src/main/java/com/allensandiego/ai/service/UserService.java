package com.allensandiego.ai.service;

import com.allensandiego.ai.dto.RegistrationRequest;
import com.allensandiego.ai.repository.UserRepository;
import com.allensandiego.ai.model.User;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(identifier)
                .map(user -> org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("No user found"));
    }

    public User register(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("Password confirmation does not match");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }
    
    public boolean authenticate(String username, String password) {
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        User user = userRepository.findByUsernameOrEmail(username).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        return passwordEncoder.matches(password, user.getPassword());
    }
}
