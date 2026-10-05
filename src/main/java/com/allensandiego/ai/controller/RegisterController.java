package com.allensandiego.ai.controller;

import com.allensandiego.ai.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final RegistrationService registrationService;

    @Autowired
    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        return "authentication/register";
    }

    @PostMapping("/register")
    public String handleRegistration(String username, String email, String password, Model model) {
        try {
            registrationService.registerUser(username, email, password);
            // Redirect to login with success message could be added here
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "authentication/register";
        }
    }

}
