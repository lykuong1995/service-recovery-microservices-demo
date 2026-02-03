package com.kuong.order.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaPortConfig {

    @Bean
    public ApplicationListener<WebServerInitializedEvent> eurekaPortListener(
            EurekaInstanceConfigBean instanceConfigBean
    ) {
        return event -> {
            int port = event.getWebServer().getPort();
            instanceConfigBean.setNonSecurePort(port);
            instanceConfigBean.setInstanceId(instanceConfigBean.getAppname() + ":" + port);
        };
    }
}
