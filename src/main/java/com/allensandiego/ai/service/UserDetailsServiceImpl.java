package com.allensandiego.ai.service;

import com.allensandiego.ai.entity.User;
import com.allensandiego.ai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("=== UserDetailsService.loadUserByUsername called ===");
        logger.info("Loading user for email: {}", email);
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Empty or null email received!");
            throw new UsernameNotFoundException("Email cannot be empty");
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        logger.info("Found user: {} with password hash length: {}", user.getEmail(), user.getPassword().length());
        UserDetails userDetails = User.build(user);
        logger.info("Built UserDetails for username: {}", userDetails.getUsername());
        return userDetails;
    }
}
