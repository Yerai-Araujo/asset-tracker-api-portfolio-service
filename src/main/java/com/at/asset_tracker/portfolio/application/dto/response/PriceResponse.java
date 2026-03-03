package com.at.asset_tracker.portfolio.application.dto.response;

import java.math.BigDecimal;

public record PriceResponse(
        String symbol,
        String type,
        BigDecimal price
) {}