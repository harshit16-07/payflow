package com.payflow.payment.controller;

import com.payflow.payment.dto.CreatePaymentRequest;
import com.payflow.payment.entity.Payment;
import com.payflow.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment( @RequestHeader("Idempotency-Key") String idempotencyKey ,@Valid  @RequestBody CreatePaymentRequest createPaymentRequest) {
        Payment payment= new Payment();
        payment.setAmount(createPaymentRequest.amount());
        payment.setCurrency(createPaymentRequest.currency());
        payment.setIdempotencyKey(idempotencyKey);
        return paymentService.createPayment(payment);
    }

    @GetMapping
    public List<Payment>getAllPayments()
    {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id)
    {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/reference/{paymentReference}")
    public Payment getPaymentByReference(@PathVariable String paymentReference)
    {
        return paymentService.getPaymentByReference(paymentReference);
    }
}
