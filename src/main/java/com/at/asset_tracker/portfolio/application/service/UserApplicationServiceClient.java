package com.at.asset_tracker.portfolio.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.at.asset_tracker.portfolio.application.dto.response.UserResponse;
import com.at.asset_tracker.portfolio.domain.exception.ResourceNotFoundException;

@Component
public class UserApplicationServiceClient {

    private final WebClient webClient;

    public UserApplicationServiceClient(@Qualifier("userWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public UserResponse validateUserExists(Long userId) {
        try {
            return webClient.get()
                    .uri("/api/users/validate/user/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        } catch (WebClientResponseException e) {
            System.out.println("Status: " + e.getRawStatusCode());
            System.out.println("Response body: " + e.getResponseBodyAsString());
            throw e;
        }
    }

}