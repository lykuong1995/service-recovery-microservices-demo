package com.kuong.order.service;

import com.kuong.order.OrderStatus;
import com.kuong.order.dto.OrderItemRequest;
import com.kuong.order.entity.Order;
import com.kuong.order.entity.OrderItem;
import com.kuong.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order create(String username, List<OrderItemRequest> itemsRequest) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PROCESSING);
        order.setUsername(username);
        order.setRetryCount(0);
        order.setMaxRetry(3);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : itemsRequest) {

            BigDecimal itemTotal =
                    itemRequest.getPrice()
                            .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            total = total.add(itemTotal);

            OrderItem item = OrderItem.builder()
                    .productName(itemRequest.getProductName())
                    .quantity(itemRequest.getQuantity())
                    .price(itemRequest.getPrice())
                    .order(order)
                    .build();

            order.getItems().add(item);
        }

        order.setTotalAmount(total);

        order = orderRepository.save(order);

        simulateProcessing(order);

        return orderRepository.save(order);
    }

    private void simulateProcessing(Order order) {
    }


    public List<Order> getUserOrders(String username) {
        // Username field was removed from Order entity,
        // so return all orders for now
        return orderRepository.findAll();
    }
}

