package com.allensandiego.ai.controller;

import com.allensandiego.ai.dto.RegistrationRequest;
import com.allensandiego.ai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/authentication/login")
    public String login() {
        return "authentication/login";
    }

    @GetMapping("/authentication/register")
    public String register(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "authentication/register";
    }

    @PostMapping("/authentication/login")
    public String login(String username, String password, BindingResult result) {
        if (result.hasErrors()) {
            return "authentication/login";
        }
        try {
            userService.authenticate(username, password);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            return "redirect:/authentication/login?error=true";
        }
    }
    
    @PostMapping("/authentication/register")
    public String register(@Valid RegistrationRequest request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("registrationRequest", request);
            return "authentication/register";
        }
        try {
            userService.register(request);
            return "redirect:/authentication/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registrationRequest", request);
            model.addAttribute("error", e.getMessage());
            return "authentication/register";
        }
    }
}
