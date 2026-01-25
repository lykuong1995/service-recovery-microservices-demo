package com.kuong.order.controller;

import com.kuong.order.dto.OrderRequest;
import com.kuong.order.entity.Order;
import com.kuong.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order create(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return orderService.create(username, request.getAmount());
    }

    @GetMapping("/my")
    public List<Order> myOrders(Authentication authentication) {
        String username = authentication.getName();
        return orderService.getUserOrders(username);
    }
}

