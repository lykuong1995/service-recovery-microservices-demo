package com.kuong.cart.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        Long id,
        String username,
        List<CartItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
