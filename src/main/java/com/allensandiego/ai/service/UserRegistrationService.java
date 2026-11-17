package com.allensandiego.ai.service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private static final String ROLE_USER_CODE = "ROLE_USER";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean register(String username, String email, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return false;
        }

        String normalizedUsername = username.trim();
        String normalizedEmail = (email == null || email.isBlank()) ? null : email.trim();

        Long existingUsers = jdbcTemplate.queryForObject(
                "select count(*) from users where username = ?", Long.class, normalizedUsername);
        if (existingUsers != null && existingUsers > 0) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into users (username, password, email, enabled) values (?, ?, ?, true)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, normalizedUsername);
            ps.setString(2, encodedPassword);
            ps.setString(3, normalizedEmail);
            return ps;
        }, keyHolder);

        Long generatedId = null;
        for (Map<String, Object> key : keyHolder.getKeyList()) {
            if (key.containsKey("ID")) {
                generatedId = ((Number) key.get("ID")).longValue();
                break;
            }
        }
        if (generatedId == null) {
            return false;
        }

        List<Long> roleIds = jdbcTemplate.queryForList(
                "select id from roles where code = ?", Long.class, ROLE_USER_CODE);
        if (roleIds.isEmpty()) {
            throw new IllegalStateException("ROLE_USER role not found in database");
        }

        jdbcTemplate.update(
                "insert into user_roles (user_id, role_id) values (?, ?)",
                generatedId.longValue(),
                roleIds.get(0));

        return true;
    }
}
