package com.allensandiego.ai.controller;

import com.allensandiego.ai.service.RegistrationRequest;
import com.allensandiego.ai.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/authentication")
@Slf4j
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "authentication/login";
    }

    @GetMapping("/register")
    public String register() {
        return "authentication/register";
    }

    @PostMapping("/register")
    public String postRegister(@Valid RegistrationRequest request, Model model) {
        try {
            userService.register(request);
            log.info("User registered successfully: {}", request.getUsername());
            return "redirect:/authentication/login";
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed for user {}: {}", request.getUsername(), e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "authentication/register";
        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", e.getMessage());
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "authentication/register";
        }
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            Model model) {
        try {
            // Let Spring Security handle the authentication
            // We only validate that the user exists here for better error messages
            userService.loadUserByUsername(username);
            log.info("Login attempt for user: {}", username);
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            log.warn("Invalid login credentials for user: {}", username);
            model.addAttribute("error", e.getMessage());
            return "authentication/login";
        } catch (Exception e) {
            // Invalid password or other authentication error - handled by Spring Security
            log.debug("Authentication failed for user: {}", username);
            model.addAttribute("error", "Invalid username or password");
            return "authentication/login";
        }
    }

}
