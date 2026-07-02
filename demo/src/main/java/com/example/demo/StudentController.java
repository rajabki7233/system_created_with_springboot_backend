package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    // Create a new student
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody StudentEntity student) {
        try {
            if (studentRepository.existsByEmail(student.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Email already exists!");
                return ResponseEntity.badRequest().body(error);
            }
            
            student.setCreatedAt(LocalDateTime.now());
            student.setUpdatedAt(LocalDateTime.now());
            student.setTotalFees(0.0);
            student.setPaidFees(0.0);
            student.setOutstandingBalance(0.0);
            student.setPaymentStatus("PENDING");
            
            StudentEntity savedStudent = studentRepository.save(student);
            return ResponseEntity.ok(savedStudent);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to create student: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get all students
    @GetMapping
    public List<StudentEntity> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        StudentEntity student = studentRepository.findById(id)
                .orElse(null);
        if (student != null) {
            return ResponseEntity.ok(student);
        }
        return ResponseEntity.badRequest().body("Student not found");
    }

    // Update student
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody StudentEntity studentDetails) {
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setAge(studentDetails.getAge());
        student.setStudentClass(studentDetails.getStudentClass());
        student.setTotalFees(studentDetails.getTotalFees());
        student.setUpdatedAt(LocalDateTime.now());
        
        StudentEntity updatedStudent = studentRepository.save(student);
        return ResponseEntity.ok(updatedStudent);
    }
}