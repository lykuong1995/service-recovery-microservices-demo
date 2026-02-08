package com.kuong.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CartItemRequest(
        @NotNull Long productId,
        @NotBlank String productName,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(1) Integer quantity
) {
}
