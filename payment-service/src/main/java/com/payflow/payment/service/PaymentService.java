package com.payflow.payment.service;


import com.payflow.payment.entity.Payment;
import com.payflow.payment.entity.PaymentStatus;
import com.payflow.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {


        private final  PaymentRepository paymentRepository;

        public PaymentService(PaymentRepository paymentRepository) {
            this.paymentRepository = paymentRepository;
        }
        public Payment createPayment(Payment payment) {
            payment.setPaymentReference("PAY-" + UUID.randomUUID().toString());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());
            return paymentRepository.save(payment);


    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id)
    {
        return paymentRepository.findById(id).orElseThrow(()->new RuntimeException("Payment not found with id : "+id));
    }

    public Payment getPaymentByReference(String paymentReference)
    {
        return paymentRepository.findByPaymentReference(paymentReference).orElseThrow(()->new RuntimeException("Payment not found with reference : "+paymentReference));
    }
}

