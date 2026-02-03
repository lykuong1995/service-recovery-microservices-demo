package com.kuong.order.controller;

import com.kuong.order.entity.Order;
import com.kuong.order.repository.OrderRepository;
import com.kuong.order.OrderStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Slf4j
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
    @CircuitBreaker(name = "orderRetry", fallbackMethod = "retryFallback")
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

    private Order retryFallback(Long id, Throwable ex) {
        log.warn("Order retry fallback for {} due to {}", id, ex.getMessage());
        return orderRepository.findById(id).orElseThrow();
    }
}
