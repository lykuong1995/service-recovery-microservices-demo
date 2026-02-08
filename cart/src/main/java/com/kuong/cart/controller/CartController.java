package com.kuong.cart.controller;

import com.kuong.cart.dto.CartItemRequest;
import com.kuong.cart.dto.CartResponse;
import com.kuong.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @PostMapping("/items")
    public CartResponse addItem(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request
    ) {
        return cartService.addItem(authentication.getName(), request);
    }

    @DeleteMapping("/items/{id}")
    public CartResponse removeItem(Authentication authentication, @PathVariable Long id) {
        return cartService.removeItem(authentication.getName(), id);
    }

    @PostMapping("/checkout")
    public Object checkout(
            Authentication authentication,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return cartService.checkout(authentication.getName(), authHeader);
    }
}
