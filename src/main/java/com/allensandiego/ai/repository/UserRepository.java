package com.allensandiego.ai.repository;

import com.allensandiego.ai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username.
     * @param username the username to search for
     * @return the User if found, null otherwise
     */
    User findByUsername(String username);

    /**
     * Check if a username exists in the database.
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

}
