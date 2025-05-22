package com.university.educationPackage.services;

import com.university.educationPackage.models.Payment;
import com.university.educationPackage.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(Payment payment) {
        // Validar pago y conectarse con pasarela de pago
        payment.setStatus("COMPLETED");
        return paymentRepository.save(payment);
    }

    public Payment getPaymentStatus(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }
}
