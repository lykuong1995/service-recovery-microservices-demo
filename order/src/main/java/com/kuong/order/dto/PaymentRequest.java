package com.kuong.order.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull Long orderId
) {
}
