package com.at.asset_tracker.portfolio.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${market.service.url}")
    private String marketServiceUrl;

    @Bean("marketWebClient")
    public WebClient marketWebClient() {
        return WebClient.builder()
                .baseUrl(marketServiceUrl)
                .build();
    }
}