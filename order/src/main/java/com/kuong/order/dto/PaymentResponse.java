package com.kuong.order.dto;

public record PaymentResponse(
        Long orderId,
        String status,
        String transactionId
) {
    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }
}
