package com.kuong.payment.service;

import com.kuong.payment.PaymentStatus;
import com.kuong.payment.dto.PaymentRequest;
import com.kuong.payment.dto.PaymentResponse;
import com.kuong.payment.entity.Payment;
import com.kuong.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Random random = new Random();

    public PaymentResponse process(PaymentRequest request) {
        boolean success = random.nextBoolean();
        PaymentStatus status = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        String transactionId = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .status(status)
                .transactionId(transactionId)
                .build();

        paymentRepository.save(payment);

        return new PaymentResponse(payment.getOrderId(), payment.getStatus().name(), payment.getTransactionId());
    }
}
