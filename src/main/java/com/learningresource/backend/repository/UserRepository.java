package com.learningresource.backend.repository;

import com.learningresource.backend.model.User;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // 1. Fetch all users
    public List<User> findAll() {
        return jdbcClient.sql("SELECT id, username, email, password_hash, created_at FROM users ORDER BY id ASC")
                .query(User.class)
                .list();
    }

    // 2. Fetch user by ID
    public Optional<User> findById(Long id) {
        return jdbcClient.sql("SELECT id, username, email, password_hash, created_at FROM users WHERE id = :id")
                .param("id", id)
                .query(User.class)
                .optional();
    }

    // 3. Fetch user by username
    public Optional<User> findByUsername(String username) {
        return jdbcClient.sql("SELECT id, username, email, password_hash, created_at FROM users WHERE username = :username")
                .param("username", username)
                .query(User.class)
                .optional();
    }

    // 4. Create a new user (with password_hash)
    public Long create(String username, String email, String passwordHash) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("""
                INSERT INTO users (username, email, password_hash)
                VALUES (:username, :email, :passwordHash)
                """)
                .param("username", username)
                .param("email", email)
                .param("passwordHash", passwordHash)
                .update(keyHolder, "id");

        Number key = keyHolder.getKey();
        return (key != null) ? key.longValue() : null;
    }

    // 5. Delete user by ID
    public boolean deleteById(Long id) {
        int rowsAffected = jdbcClient.sql("DELETE FROM users WHERE id = :id")
                .param("id", id)
                .update();
        return rowsAffected > 0;
    }
}