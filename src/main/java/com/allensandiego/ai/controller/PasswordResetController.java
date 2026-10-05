package com.allensandiego.ai.controller;

import com.allensandiego.ai.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "authentication/reset-password";
    }

    @PostMapping("/reset-password")
    public String handlePasswordReset(String username, Model model) {
        // Try to reset password - returns false if user not found
        boolean success = passwordResetService.resetPassword(username);
        
        if (!success) {
            model.addAttribute("error", "Invalid username or password");
            return "authentication/reset-password";
        }
        
        return "authentication/password-changed";
    }

}
