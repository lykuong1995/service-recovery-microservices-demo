package com.kuong.payment.dto;

public record PaymentResponse(
        Long orderId,
        String status,
        String transactionId
) {
}
