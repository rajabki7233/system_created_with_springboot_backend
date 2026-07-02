package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByIsReadFalse();
    List<NotificationEntity> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<NotificationEntity> findAllByOrderByCreatedAtDesc();
}