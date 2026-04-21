package com.at.asset_tracker.portfolio.domain.events.marketWebEvents;

import java.math.BigDecimal;

public record AssetsValueCalculatedEvent(
        Long portfolioId,
        BigDecimal totalValue) {
}
