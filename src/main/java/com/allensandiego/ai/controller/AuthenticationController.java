package com.allensandiego.ai.controller;

import com.allensandiego.ai.model.User;
import com.allensandiego.ai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/authentication")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "authentication/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "authentication/register";
    }

    @PostMapping("/register")
    public String postRegister(Model model, String username, String password, String acceptTermsAndConditions) {
        // Validation: username not empty
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username is required");
            return "authentication/register";
        }

        // Validation: password not empty
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Password is required");
            return "authentication/register";
        }

        // Validation: accept terms and conditions checked
        if ("false".equals(acceptTermsAndConditions)) {
            model.addAttribute("error", "You must accept the terms and conditions to register");
            return "authentication/register";
        }

        // Check if username already exists
        if (userRepository.existsByUsername(username.trim())) {
            model.addAttribute("error", "Username already exists. Please use a different username.");
            return "authentication/register";
        }

        // Create new user with hashed password and default role
        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setAuthorities("ROLE_USER");
        newUser.setEnabled(true);

        // Save to database directly (no email verification)
        userRepository.save(newUser);

        return "redirect:/authentication/login";
    }

}
