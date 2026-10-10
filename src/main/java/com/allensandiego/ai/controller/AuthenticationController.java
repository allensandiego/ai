package com.allensandiego.ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authentication")
public class AuthenticationController {

    @GetMapping({"/login", "/login.html"})
    public String login() {
        return "authentication/login";
    }

    @GetMapping({"/register", "/register.html"})
    public String register() {
        return "authentication/register";
    }

    @GetMapping({"/reset-password", "/reset-password.html"})
    public String resetPassword() {
        return "authentication/reset-password";
    }

    @GetMapping({"/change-password", "/change-password.html"})
    public String changePassword() {
        return "authentication/change-password";
    }

    @GetMapping({"/check-email", "/check-email.html"})
    public String checkEmail() {
        return "authentication/check-email";
    }

    @GetMapping({"/password-changed", "/password-changed.html"})
    public String passwordChanged() {
        return "authentication/password-changed";
    }

}