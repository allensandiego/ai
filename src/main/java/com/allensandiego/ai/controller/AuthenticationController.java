package com.allensandiego.ai.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.allensandiego.ai.service.RegistrationService;

@Controller
@RequestMapping("/authentication")
public class AuthenticationController {

    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(RegistrationService registrationService,
                                     AuthenticationManager authenticationManager) {
        this.registrationService = registrationService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String login() {
        return "authentication/login";
    }

    @PostMapping("/do-login")
    public String doLogin(Model model, HttpSession session,
                           @RequestParam String username,
                           @RequestParam String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "error");
            return "authentication/login";
        }
    }

    @GetMapping("/register")
    public String register() {
        return "authentication/register";
    }

    @PostMapping("/register")
    public String postRegister(Model model,
                                @RequestParam String username,
                                @RequestParam(required = false) String email,
                                @RequestParam String password) {
        if (registrationService.existsByUsername(username)) {
            model.addAttribute("error", "email");
            return "authentication/register";
        }
        registrationService.register(username, email, password);
        return "redirect:/authentication/login";
    }

}
