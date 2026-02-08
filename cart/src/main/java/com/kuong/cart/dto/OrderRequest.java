package com.kuong.cart.dto;

import java.util.List;

public record OrderRequest(
        List<OrderItemRequest> items
) {
}
