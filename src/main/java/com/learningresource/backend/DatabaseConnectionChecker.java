package com.learningresource.backend;

import com.learningresource.backend.model.User;
import com.learningresource.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseConnectionChecker implements CommandLineRunner {

    private final UserRepository userRepository;

    public DatabaseConnectionChecker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("==================================================");
        System.out.println("TESTING USER REPOSITORY (JdbcClient)");

        String testUsername = "dev_tester";
        String testEmail = "tester@example.com";
        String testHash = "$2a$10$dummyhashedpasswordvaluehere";

        Optional<User> existingUser = userRepository.findByUsername(testUsername);

        Long userId;
        if (existingUser.isEmpty()) {
            userId = userRepository.create(testUsername, testEmail, testHash);
            System.out.println("Created new user with ID: " + userId);
        } else {
            userId = existingUser.get().id();
            System.out.println("Found existing user with ID: " + userId);
        }

        userRepository.findById(userId).ifPresentOrElse(
            user -> {
                System.out.println("Retrieved User: " + user);
                System.out.println("Username: " + user.username());
                System.out.println("Password Hash: " + user.passwordHash());
                System.out.println("Created At: " + user.createdAt());
            },
            () -> System.err.println("Failed to fetch user with ID: " + userId)
        );

        System.out.println("Total users in database: " + userRepository.findAll().size());
        System.out.println("==================================================");
    }
}