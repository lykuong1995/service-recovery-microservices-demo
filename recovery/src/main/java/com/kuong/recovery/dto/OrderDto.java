package com.kuong.recovery.dto;

import lombok.Data;

@Data
public class OrderDto {

    private Long id;
    private String status;
    private int retryCount;
    private int maxRetry;
}

