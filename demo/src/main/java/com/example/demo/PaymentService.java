package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public PaymentEntity processPayment(PaymentRequest paymentRequest) {
        // Get student
        StudentEntity student = studentRepository.findById(paymentRequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Create payment
        PaymentEntity payment = new PaymentEntity();
        payment.setStudent(student);
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setTransactionId(paymentRequest.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("COMPLETED");
        payment.setNotes(paymentRequest.getNotes());
        payment.setProcessedBy(paymentRequest.getProcessedBy());
        payment.setCreatedAt(LocalDateTime.now());
        
        // Generate receipt number
        String receiptNumber = generateReceiptNumber();
        payment.setReceiptNumber(receiptNumber);
        
        // Save payment
        PaymentEntity savedPayment = paymentRepository.save(payment);
        
        // Update student fees
        updateStudentFees(student, paymentRequest.getAmount());
        
        // Create notification
        createPaymentNotification(student, payment);
        
        return savedPayment;
    }
    
    private void updateStudentFees(StudentEntity student, Double amount) {
        // Update paid fees
        student.setPaidFees(student.getPaidFees() + amount);
        
        // Calculate outstanding balance
        double outstanding = student.getTotalFees() - student.getPaidFees();
        student.setOutstandingBalance(Math.max(0, outstanding));
        
        // Update payment status
        if (student.getPaidFees() >= student.getTotalFees()) {
            student.setPaymentStatus("PAID");
        } else if (student.getPaidFees() > 0) {
            student.setPaymentStatus("PARTIAL");
        } else {
            student.setPaymentStatus("PENDING");
        }
        
        student.setUpdatedAt(LocalDateTime.now());
        studentRepository.save(student);
    }
    
    private void createPaymentNotification(StudentEntity student, PaymentEntity payment) {
        NotificationEntity notification = new NotificationEntity();
        notification.setTitle("Payment Received");
        notification.setMessage(String.format(
            "%s made a payment of KES %.2f via %s. Receipt #: %s",
            student.getName(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getReceiptNumber()
        ));
        notification.setType("PAYMENT_RECEIVED");
        notification.setStudent(student);
        notification.setReferenceId(payment.getReceiptNumber());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        notificationService.saveNotification(notification);
    }
    
    private String generateReceiptNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "RCP-" + timestamp + "-" + random;
    }
    
    public List<PaymentEntity> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<PaymentEntity> getPaymentsByStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }
    
    public PaymentEntity getPaymentByReceipt(String receiptNumber) {
        return paymentRepository.findByReceiptNumber(receiptNumber);
    }
}