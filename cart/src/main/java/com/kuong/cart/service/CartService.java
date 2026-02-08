package com.kuong.cart.service;

import com.kuong.cart.dto.*;
import com.kuong.cart.entity.Cart;
import com.kuong.cart.entity.CartItem;
import com.kuong.cart.repository.CartItemRepository;
import com.kuong.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RestClient restClient;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public CartResponse getCart(String username) {
        Cart cart = cartRepository.findByUsername(username)
                .orElseGet(() -> createCart(username));

        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getId()).stream()
                .map(this::toItemResponse)
                .toList();

        return new CartResponse(
                cart.getId(),
                cart.getUsername(),
                items,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }

    @Transactional
    public CartResponse addItem(String username, CartItemRequest request) {
        Cart cart = cartRepository.findByUsername(username)
                .orElseGet(() -> createCart(username));

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(request.productId());
        item.setProductName(request.productName());
        item.setPrice(request.price());
        item.setQuantity(request.quantity());

        cartItemRepository.save(item);

        return getCart(username);
    }

    @Transactional
    public CartResponse removeItem(String username, Long itemId) {
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        cartItemRepository.delete(item);
        return getCart(username);
    }

    @Transactional
    public Object checkout(String username, String authHeader) {
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        List<OrderItemRequest> orderItems = items.stream()
                .map(item -> new OrderItemRequest(item.getProductId(), item.getQuantity()))
                .toList();

        OrderRequest orderRequest = new OrderRequest(orderItems);

        Object response = restClient.post()
                .uri(orderServiceUrl + "/orders")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .body(Object.class);

        cartItemRepository.deleteAll(items);

        return response;
    }

    private Cart createCart(String username) {
        Cart cart = new Cart();
        cart.setUsername(username);
        return cartRepository.save(cart);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
