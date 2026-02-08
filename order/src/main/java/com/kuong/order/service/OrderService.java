package com.kuong.order.service;

import com.kuong.order.OrderStatus;
import com.kuong.order.dto.InventoryRequest;
import com.kuong.order.dto.OrderItemRequest;
import com.kuong.order.dto.PaymentRequest;
import com.kuong.order.dto.PaymentResponse;
import com.kuong.order.dto.ProductResponse;
import com.kuong.order.entity.Order;
import com.kuong.order.entity.OrderItem;
import com.kuong.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestClient restClient;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    public Order create(String username, List<OrderItemRequest> itemsRequest, String authHeader) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setUsername(username);
        order.setRetryCount(0);
        order.setMaxRetry(3);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : itemsRequest) {
            ProductResponse product = fetchProduct(itemRequest.getProductId(), authHeader);
            BigDecimal itemTotal =
                    product.price()
                            .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            total = total.add(itemTotal);

            OrderItem item = OrderItem.builder()
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .price(product.price())
                    .order(order)
                    .build();

            order.getItems().add(item);
        }

        order.setTotalAmount(total);

        boolean reserved = reserveInventory(order, authHeader);
        order.setStatus(reserved ? OrderStatus.PROCESSING : OrderStatus.FAILED_TEMP);
        order = orderRepository.save(order);

        if (reserved) {
            boolean paid = processPayment(order, authHeader);
            order.setStatus(paid ? OrderStatus.COMPLETED : OrderStatus.FAILED_TEMP);
            if (!paid) {
                releaseInventory(buildInventoryRequests(order), authHeader);
            }
            order = orderRepository.save(order);
        }

        return order;
    }

    public Order retry(Long orderId, String authHeader) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        if (order.getRetryCount() >= order.getMaxRetry()) {
            order.setStatus(OrderStatus.FAILED_FINAL);
            return orderRepository.save(order);
        }

        order.setRetryCount(order.getRetryCount() + 1);

        boolean reserved = reserveInventory(order, authHeader);
        order.setStatus(reserved ? OrderStatus.PROCESSING : OrderStatus.FAILED_TEMP);
        order = orderRepository.save(order);

        if (reserved) {
            boolean paid = processPayment(order, authHeader);
            order.setStatus(paid ? OrderStatus.COMPLETED : OrderStatus.FAILED_TEMP);
            if (!paid) {
                releaseInventory(buildInventoryRequests(order), authHeader);
            }
            order = orderRepository.save(order);
        }

        return order;
    }

    public List<Order> getUserOrders(String username) {
        return orderRepository.findByusername(username);
    }

    private ProductResponse fetchProduct(Long productId, String authHeader) {
        return restClient.get()
                .uri(productServiceUrl + "/products/" + productId)
                .headers(headers -> applyAuth(headers, authHeader))
                .retrieve()
                .body(ProductResponse.class);
    }

    private boolean reserveInventory(Order order, String authHeader) {
        List<InventoryRequest> reserved = new ArrayList<>();

        try {
            for (InventoryRequest request : buildInventoryRequests(order)) {
                restClient.post()
                        .uri(inventoryServiceUrl + "/inventory/reserve")
                        .headers(headers -> applyAuth(headers, authHeader))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .toBodilessEntity();

                reserved.add(request);
            }
            return true;
        } catch (RestClientResponseException ex) {
            releaseInventory(reserved, authHeader);
            return false;
        }
    }

    private void releaseInventory(List<InventoryRequest> reserved, String authHeader) {
        for (InventoryRequest request : reserved) {
            restClient.post()
                    .uri(inventoryServiceUrl + "/inventory/release")
                    .headers(headers -> applyAuth(headers, authHeader))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        }
    }

    private boolean processPayment(Order order, String authHeader) {
        try {
            PaymentResponse response = restClient.post()
                    .uri(paymentServiceUrl + "/payments/process")
                    .headers(headers -> applyAuth(headers, authHeader))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PaymentRequest(order.getId()))
                    .retrieve()
                    .body(PaymentResponse.class);

            return response != null && response.isSuccess();
        } catch (RestClientResponseException ex) {
            return false;
        }
    }

    private List<InventoryRequest> buildInventoryRequests(Order order) {
        List<InventoryRequest> requests = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            requests.add(new InventoryRequest(item.getProductId(), item.getQuantity()));
        }
        return requests;
    }

    private void applyAuth(HttpHeaders headers, String authHeader) {
        if (authHeader != null && !authHeader.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        }
    }
}
