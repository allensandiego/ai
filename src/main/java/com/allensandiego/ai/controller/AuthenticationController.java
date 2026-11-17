package com.allensandiego.ai.controller;

import com.allensandiego.ai.service.UserRegistrationService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/authentication")
public class AuthenticationController {

    private final UserRegistrationService userRegistrationService;

    public AuthenticationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
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
    public String postRegister(@RequestParam(required = false) String username,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return "redirect:/authentication/register?error";
        }

        boolean registered = userRegistrationService.register(username, email, password);
        if (!registered) {
            return "redirect:/authentication/register?error";
        }

        return "redirect:/authentication/login?registered=true";
    }

}
