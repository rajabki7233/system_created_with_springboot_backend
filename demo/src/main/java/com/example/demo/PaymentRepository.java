package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByStudentId(Long studentId);
    List<PaymentEntity> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    List<PaymentEntity> findByPaymentStatus(String status);
    PaymentEntity findByReceiptNumber(String receiptNumber);
}