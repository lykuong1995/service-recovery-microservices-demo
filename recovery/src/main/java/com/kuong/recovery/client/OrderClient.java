package com.kuong.recovery.client;

import com.kuong.recovery.dto.OrderDto;
import com.kuong.recovery.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderClient {

    private final RestTemplate restTemplate;
    private final JwtService jwtService;

    @Value("${recovery.order-service.base-url}")
    private String baseUrl;

    private HttpHeaders buildHeaders() {
        String token = jwtService.generateSystemToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        System.out.println("SYSTEM TOKEN: " + token);

        return headers;
    }

    public List<OrderDto> getRecoverable() {

        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        ResponseEntity<OrderDto[]> response =
                restTemplate.exchange(
                        baseUrl + "/internal/recoverable",
                        HttpMethod.GET,
                        entity,
                        OrderDto[].class
                );

        return Arrays.asList(response.getBody());
    }

    public void retry(Long orderId) {

        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        restTemplate.exchange(
                baseUrl + "/internal/retry/" + orderId,
                HttpMethod.POST,
                entity,
                Void.class
        );
    }
}


