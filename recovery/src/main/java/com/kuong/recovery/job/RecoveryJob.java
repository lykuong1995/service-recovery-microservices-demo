package com.kuong.recovery.job;

import com.kuong.recovery.client.OrderClient;
import com.kuong.recovery.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecoveryJob {

    private final OrderClient orderClient;

    @Scheduled(fixedDelayString = "${recovery.schedule.fixed-delay}")
    public void recover() {

        log.info("Recovery job triggered...");

        List<OrderDto> orders = orderClient.getRecoverable();

        for (OrderDto order : orders) {

            log.info("Retrying order {}", order.getId());

            orderClient.retry(order.getId());
        }
    }
}