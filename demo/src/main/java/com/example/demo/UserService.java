package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get all users
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get user by email
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Get users by name (contains)
    public List<UserEntity> getUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    // Get users older than age
    public List<UserEntity> getUsersOlderThan(int age) {
        return userRepository.findByAgeGreaterThan(age);
    }

    // Create new user
    public UserEntity createUser(UserEntity user) {
        // Set creation timestamp
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    // Create multiple users (Bulk insert)
    public List<UserEntity> createMultipleUsers(List<UserEntity> users) {
        // Check for duplicate emails
        for (UserEntity user : users) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Email already exists: " + user.getEmail());
            }
            user.setCreatedAt(LocalDateTime.now());
            user.setIsActive(true);
        }
        return userRepository.saveAll(users);
    }

    // Update user
    public UserEntity updateUser(Long id, UserEntity userDetails) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setAge(userDetails.getAge());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }

        return userRepository.save(user);
    }

    // Delete user
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    // Delete user by email
    public void deleteUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        userRepository.delete(user);
    }

    // Count users
    public long countUsers() {
        return userRepository.count();
    }
    
    // Get users by age range
    public List<UserEntity> getUsersByAgeRange(int minAge, int maxAge) {
        return userRepository.findByAgeBetween(minAge, maxAge);
    }
}