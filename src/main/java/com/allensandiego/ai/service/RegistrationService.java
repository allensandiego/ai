package com.allensandiego.ai.service;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.jdbc = new JdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String username, String email, String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword);

        jdbc.update(
            "insert into users (username, password, enabled, authorities) values (?, ?, true, ?)",
            username, hashed, email
        );

        Long userId = jdbc.queryForObject(
            "select id from users where username = ?",
            Long.class,
            username
        );

        jdbc.update(
            "insert into user_roles (user_id, role_id, enabled) select ?, id, true from roles where code = 'ROLE_USER'",
            userId
        );
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbc.queryForObject(
            "select count(*) from users where username = ?",
            Integer.class,
            username
        );
        return count != null && count > 0;
    }

}
