package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Process a payment (POST request from Postman)
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentEntity payment = paymentService.processPayment(paymentRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment processed successfully!");
            response.put("payment", payment);
            response.put("receipt_number", payment.getReceiptNumber());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Payment processing failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get all payments
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        List<PaymentEntity> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // Get payments by student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getPaymentsByStudent(@PathVariable Long studentId) {
        List<PaymentEntity> payments = paymentService.getPaymentsByStudent(studentId);
        return ResponseEntity.ok(payments);
    }

    // Get payment by receipt number
    @GetMapping("/receipt/{receiptNumber}")
    public ResponseEntity<?> getPaymentByReceipt(@PathVariable String receiptNumber) {
        PaymentEntity payment = paymentService.getPaymentByReceipt(receiptNumber);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        }
        return ResponseEntity.badRequest().body("Payment not found");
    }
}