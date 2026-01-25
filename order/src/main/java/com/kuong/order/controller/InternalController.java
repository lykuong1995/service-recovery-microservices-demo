package com.kuong.order.controller;

import com.kuong.order.entity.Order;
import com.kuong.order.repository.OrderRepository;
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
        return orderRepository.findByStatusAndRetryCountLessThan("FAILED", 3);
    }

    @PostMapping("/retry/{id}")
    public Order retry(@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow();

        if (order.getRetryCount() >= order.getMaxRetry()) {
            order.setStatus("PERMANENT_FAILURE");
            return orderRepository.save(order);
        }

        order.setRetryCount(order.getRetryCount() + 1);

        boolean success = new Random().nextBoolean();

        if (success) {
            order.setStatus("COMPLETED");
        } else {
            order.setStatus("FAILED");
        }

        return orderRepository.save(order);
    }
}
