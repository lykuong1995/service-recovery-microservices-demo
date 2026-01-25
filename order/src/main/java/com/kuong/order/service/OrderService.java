package com.kuong.order.service;

import com.kuong.order.entity.Order;
import com.kuong.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order create(String username, BigDecimal amount) {

        Order order = Order.builder()
                .username(username)
                .amount(amount)
                .status("PENDING")
                .retryCount(0)
                .maxRetry(3)
                .build();

        order = orderRepository.save(order);

        // Simulate external processing
        boolean success = new Random().nextBoolean();

        if (success) {
            order.setStatus("COMPLETED");
        } else {
            order.setStatus("FAILED");
        }

        return orderRepository.save(order);
    }


    public List<Order> getUserOrders(String username) {
        return orderRepository.findByUsername(username);
    }
}

