package com.university.educationPackage.repositories;

import com.university.educationPackage.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByEnrollmentId(Long enrollmentId);
}
