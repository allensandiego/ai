package com.allensandiego.ai.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JdbcUserDetailsService implements UserDetailsService {

    private static final String LOAD_USER_SQL = """
            select u.username, u.password, u.enabled, r.code
            from users u
            join user_roles ur on ur.user_id = u.id and ur.enabled
            join roles r on r.id = ur.role_id and r.enabled
            where u.username = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserRow> rows = jdbcTemplate.query(LOAD_USER_SQL, (rs, rowNum) -> new UserRow(
                rs.getString(1),
                rs.getString(2),
                rs.getBoolean(3),
                rs.getString(4)), username);

        if (rows.isEmpty()) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }

        UserRow first = rows.get(0);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (UserRow row : rows) {
            authorities.add(new SimpleGrantedAuthority(row.authority()));
        }

        return User.builder()
                .username(first.username())
                .password(first.password())
                .disabled(!first.enabled())
                .authorities(authorities)
                .build();
    }

    private record UserRow(String username, String password, boolean enabled, String authority) {
    }
}
