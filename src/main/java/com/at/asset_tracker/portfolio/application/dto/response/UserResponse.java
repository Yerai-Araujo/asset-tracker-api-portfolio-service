package com.at.asset_tracker.portfolio.application.dto.response;

public record UserResponse(
        Long id,
        String name,
        String email,
        Long portfolioId
) {}