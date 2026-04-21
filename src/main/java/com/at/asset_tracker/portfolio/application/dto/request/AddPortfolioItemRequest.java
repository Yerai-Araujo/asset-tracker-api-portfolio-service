package com.at.asset_tracker.portfolio.application.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record AddPortfolioItemRequest(
        @NotNull Long assetId,
        @NotNull BigDecimal quantity
) {
    
}
