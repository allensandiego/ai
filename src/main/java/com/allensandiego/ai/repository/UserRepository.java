package com.allensandiego.ai.repository;

import com.allensandiego.ai.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("select u from User u where u.username = :identifier or u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
}
