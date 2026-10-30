package com.learningresource.backend.controller;

import com.learningresource.backend.dto.CreateUserRequest;
import com.learningresource.backend.model.User;
import com.learningresource.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /api/users -> list all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET /api/users/{id} -> fetch single user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/users -> register/create a new user
    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody CreateUserRequest request) {
        if (request.username() == null || request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Storing basic placeholder hash until security/bcrypt is wired
        String placeholderHash = "hash_" + request.password().hashCode();

        Long newUserId = userRepository.create(request.username(), request.email(), placeholderHash);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserId);
    }

    // DELETE /api/users/{id} -> remove user (cascades resources and details)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userRepository.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}