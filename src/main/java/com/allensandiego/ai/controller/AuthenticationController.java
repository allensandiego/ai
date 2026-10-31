package com.allensandiego.ai.controller;

import com.allensandiego.ai.entity.User;
import com.allensandiego.ai.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authentication")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "authentication/login";
    }

    @GetMapping("/register")
    public String register() {
        return "authentication/register";
    }

    @PostMapping("/register")
    public String postRegister(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/authentication/register?error=true";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", true);
            model.addAttribute("message", "Username already registered. Please use a different username or login.");
            return "redirect:/authentication/register?error=true";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "redirect:/authentication/login?registered=true";
    }

}
