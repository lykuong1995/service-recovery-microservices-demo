package com.kuong.order.service;

import com.kuong.order.entity.Order;
import com.kuong.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order create(String username, BigDecimal amount) {

        Order order = Order.builder()
                .username(username)
                .amount(amount)
                .status("PENDING")
                .build();

        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(String username) {
        return orderRepository.findByUsername(username);
    }
}

