package com.payflow.payment.service;
import com.payflow.payment.exception.PaymentNotFoundException;

import com.payflow.payment.entity.Payment;
import com.payflow.payment.entity.PaymentStatus;
import com.payflow.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import com.payflow.payment.event.PaymentCreatedEvent;
import com.payflow.payment.event.PaymentEventProducer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {


        private final  PaymentRepository paymentRepository;
        private final PaymentEventProducer paymentEventProducer;
        public PaymentService(PaymentRepository paymentRepository, PaymentEventProducer paymentEventProducer) {
            this.paymentRepository = paymentRepository;
            this.paymentEventProducer=paymentEventProducer;
        }
        public Payment createPayment(Payment payment) {
            payment.setPaymentReference("PAY-" + UUID.randomUUID().toString());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());

            Payment savedPayment= paymentRepository.save(payment);

            PaymentCreatedEvent event = new PaymentCreatedEvent(
                    savedPayment.getId(),
                    savedPayment.getPaymentReference(),
                    savedPayment.getAmount(),
                    savedPayment.getCurrency(),
                    savedPayment.getCreatedAt()
            );
            paymentEventProducer.publishPaymentCreatedEvent(event);
            return savedPayment;




    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id)
    {
        return paymentRepository.findById(id).orElseThrow(()->new PaymentNotFoundException("Payment not found with id : "+id));
    }

    public Payment getPaymentByReference(String paymentReference)
    {
        return paymentRepository.findByPaymentReference(paymentReference).orElseThrow(()->new PaymentNotFoundException("Payment not found with reference : "+paymentReference));
    }
}

