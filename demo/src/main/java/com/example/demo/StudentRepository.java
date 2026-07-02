package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    Optional<StudentEntity> findByEmail(String email);
    List<StudentEntity> findByNameContainingIgnoreCase(String name);
    List<StudentEntity> findByPaymentStatus(String status);
    boolean existsByEmail(String email);
}