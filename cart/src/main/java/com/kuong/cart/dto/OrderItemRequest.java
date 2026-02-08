package com.kuong.cart.dto;

public record OrderItemRequest(
        Long productId,
        Integer quantity
) {
}
