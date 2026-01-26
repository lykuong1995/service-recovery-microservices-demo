package com.kuong.order.controller;

import com.kuong.order.entity.Order;
import com.kuong.order.repository.OrderRepository;
import com.kuong.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    private final OrderRepository orderRepository;

    @GetMapping("/recoverable")
    public List<Order> recoverable() {
        return orderRepository.findByStatusInAndRetryCountLessThan(
                List.of(OrderStatus.PROCESSING, OrderStatus.FAILED_TEMP),
                5
        );
    }

    @PostMapping("/retry/{id}")
    public Order retry(@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow();

        if (order.getRetryCount() >= order.getMaxRetry()) {
            order.setStatus(OrderStatus.FAILED_FINAL);
            return orderRepository.save(order);
        }

        order.setRetryCount(order.getRetryCount() + 1);

        boolean success = new Random().nextBoolean();

        if (success) {
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            order.setStatus(OrderStatus.FAILED_TEMP);
        }

        return orderRepository.save(order);
    }
}
