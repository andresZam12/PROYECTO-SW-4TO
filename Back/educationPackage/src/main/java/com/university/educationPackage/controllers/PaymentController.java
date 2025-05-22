package com.university.educationPackage.controllers;

import com.university.educationPackage.models.Payment;
import com.university.educationPackage.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public Payment processPayment(@RequestBody Payment payment) {
        return paymentService.processPayment(payment);
    }

    @GetMapping("/{paymentId}")
    public Payment getPaymentStatus(@PathVariable Long paymentId) {
        return paymentService.getPaymentStatus(paymentId);
    }
}
