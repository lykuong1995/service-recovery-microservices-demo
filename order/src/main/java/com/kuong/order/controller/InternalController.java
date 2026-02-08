package com.kuong.order.controller;

import com.kuong.order.OrderStatus;
import com.kuong.order.entity.Order;
import com.kuong.order.repository.OrderRepository;
import com.kuong.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Slf4j
public class InternalController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @GetMapping("/recoverable")
    public List<Order> recoverable() {
        return orderRepository.findByStatusInAndRetryCountLessThan(
                List.of(OrderStatus.PROCESSING, OrderStatus.FAILED_TEMP),
                5
        );
    }

    @PostMapping("/retry/{id}")
    @CircuitBreaker(name = "orderRetry", fallbackMethod = "retryFallback")
    public Order retry(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return orderService.retry(id, authHeader);
    }

    private Order retryFallback(Long id, String authHeader, Throwable ex) {
        log.warn("Order retry fallback for {} due to {}", id, ex.getMessage());
        return orderRepository.findById(id).orElseThrow();
    }
}
