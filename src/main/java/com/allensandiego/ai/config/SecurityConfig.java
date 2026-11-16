package com.allensandiego.ai.config;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        return username -> {
            List<Map<String, Object>> rows = jdbc.queryForList(
                """
                select u.username, u.password, u.enabled, r.code as role_code
                from users u
                left join user_roles ur on ur.user_id = u.id and ur.enabled = true
                left join roles r on r.id = ur.role_id and r.enabled = true
                where u.username = ?
                """,
                username
            );

            if (rows.isEmpty()) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            Map<String, Object> first = rows.get(0);
            String storedUsername = (String) first.get("username");
            String password = (String) first.get("password");
            boolean enabled = (Boolean) first.get("enabled");

            List<GrantedAuthority> authorities = new java.util.ArrayList<>();
            for (Map<String, Object> r : rows) {
                Object code = r.get("role_code");
                if (code != null) {
                    authorities.add(new SimpleGrantedAuthority((String) code));
                }
            }

            return User.builder()
                .username(storedUsername)
                .password(password)
                .disabled(!enabled)
                .authorities(authorities)
                .build();
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**", "/assets/**", "/css/**", "/js/**", "/vendors/**").permitAll()
                .requestMatchers("/authentication/login", "/authentication/register", "/authentication/do-login").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .logout(logout -> logout
                .logoutUrl("/authentication/logout")
                .permitAll()
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

}
