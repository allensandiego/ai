package com.allensandiego.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register","/public/**", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .successHandler((request, response, authentication) -> {
                    // Redirect to main application on success
                    response.sendRedirect("/");
                })
                .failureHandler((request, response, exception) -> {
                    // Generic error message - do not reveal if username exists
                    response.setStatus(401);
                    response.setContentType("text/plain");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("Invalid username or password");
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Clear session and redirect to login
                    response.sendRedirect("/login?logout=true");
                })
            );

        return http.build();
    }

}
