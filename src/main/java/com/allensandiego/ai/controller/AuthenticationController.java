package com.allensandiego.ai.controller;

import com.allensandiego.ai.entity.User;
import com.allensandiego.ai.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authentication")
public class AuthenticationController {

    private final RegistrationService registrationService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(RegistrationService registrationService, PasswordEncoder passwordEncoder) {
        this.registrationService = registrationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "authentication/login";
    }

    @GetMapping("/register")
    public String register() {
        return "authentication/register";
    }

    @PostMapping("/register")
    public String postRegister(Model model, HttpSession session) {
        String name = session.getAttribute("name") != null ? (String) session.getAttribute("name") : "";
        String email = session.getAttribute("email") != null ? (String) session.getAttribute("email") : "";
        String password = session.getAttribute("password") != null ? (String) session.getAttribute("password") : "";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", true);
            return "authentication/register";
        }

        try {
            registrationService.register(name, email, password);
            session.invalidate();
            return "redirect:/authentication/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", true);
            return "authentication/register";
        }
    }

}
