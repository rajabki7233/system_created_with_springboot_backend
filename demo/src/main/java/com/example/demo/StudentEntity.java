package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class StudentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private Integer age;
    
    @Column(name = "student_class")
    private String studentClass;
    
    @Column(name = "total_fees")
    private Double totalFees = 0.0;
    
    @Column(name = "paid_fees")
    private Double paidFees = 0.0;
    
    @Column(name = "outstanding_balance")
    private Double outstandingBalance = 0.0;
    
    @Column(name = "payment_status")
    private String paymentStatus = "PENDING"; // PAID, PARTIAL, PENDING, OVERDUE
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentEntity> payments = new ArrayList<>();
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String studentClass) { this.studentClass = studentClass; }
    
    public Double getTotalFees() { return totalFees; }
    public void setTotalFees(Double totalFees) { this.totalFees = totalFees; }
    
    public Double getPaidFees() { return paidFees; }
    public void setPaidFees(Double paidFees) { this.paidFees = paidFees; }
    
    public Double getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(Double outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<PaymentEntity> getPayments() { return payments; }
    public void setPayments(List<PaymentEntity> payments) { this.payments = payments; }
}