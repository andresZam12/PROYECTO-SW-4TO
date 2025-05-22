package com.university.educationPackage.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Enrollment enrollment;

    private Double amount;
    private String paymentMethod;
    private String transactionId;
    private String status;
    private LocalDateTime paymentDate;

    public void setEnrollment(Enrollment savedEnrollment) {

    }

    public void setAmount(double v) {

    }

    public void setStatus(String completed) {

    }

    // Getters y Setters
}
