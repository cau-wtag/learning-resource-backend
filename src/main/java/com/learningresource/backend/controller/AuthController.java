package com.learningresource.backend.controller;

import com.learningresource.backend.dto.AuthResponse;
import com.learningresource.backend.dto.CreateUserRequest;
import com.learningresource.backend.dto.LoginRequest;
import com.learningresource.backend.model.User;
import com.learningresource.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // POST /api/auth/register -> sign up a new account
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CreateUserRequest request) {
        if (request.username() == null || request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().body("Username, email, and password are required.");
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken.");
        }

        String placeholderHash = "hash_" + request.password().hashCode();
        Long newId = userRepository.create(request.username(), request.email(), placeholderHash);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(newId, request.username(), request.email()));
    }

    // POST /api/auth/login -> verify credentials and return user details
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.username());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }

        User user = userOpt.get();
        String expectedHash = "hash_" + request.password().hashCode();

        // In production: BCrypt.checkpw(request.password(), user.passwordHash())
        if (!expectedHash.equals(user.passwordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }

        return ResponseEntity.ok(new AuthResponse(user.id(), user.username(), user.email()));
    }
}