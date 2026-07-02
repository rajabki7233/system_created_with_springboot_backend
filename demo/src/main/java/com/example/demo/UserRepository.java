package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    // Find user by email
    Optional<UserEntity> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find users by name (contains, case insensitive)
    List<UserEntity> findByNameContainingIgnoreCase(String name);
    
    // Find users older than age
    List<UserEntity> findByAgeGreaterThan(int age);
    
    // Delete user by email
    void deleteByEmail(String email);
    
    // Find users by age range
    List<UserEntity> findByAgeBetween(int minAge, int maxAge);
}