package com.kuong.order.repository;

import com.kuong.order.OrderStatus;
import com.kuong.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndRetryCountLessThan(OrderStatus status, int retryCount);
    List<Order> findByusername(String username);
    List<Order> findByStatusInAndRetryCountLessThan(
            List<OrderStatus> statuses,
            int retryCount
    );
}
