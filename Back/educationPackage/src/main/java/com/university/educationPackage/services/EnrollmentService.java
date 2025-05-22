package com.university.educationPackage.services;

import com.university.educationPackage.models.Enrollment;
import com.university.educationPackage.models.Payment;
import com.university.educationPackage.repositories.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PaymentService paymentService;

    public Enrollment createEnrollment(Enrollment enrollment) {
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Crear pago inicial
        Payment initialPayment = new Payment();
        initialPayment.setEnrollment(savedEnrollment);
//        initialPayment.setAmount(savedEnrollment.getProgram().getPrice() * 0.1); // 10% inicial
        double price = (Double) savedEnrollment.getProgram().getPrice();
        initialPayment.setAmount(price * 0.1);
        paymentService.processPayment(initialPayment);

        return savedEnrollment;
    }

    public Enrollment getEnrollment(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));
    }
}