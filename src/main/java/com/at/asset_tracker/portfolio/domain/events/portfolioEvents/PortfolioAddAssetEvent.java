package com.at.asset_tracker.portfolio.domain.events.portfolioEvents;

import java.math.BigDecimal;

public record PortfolioAddAssetEvent(
        Long portfolioId,
        Long assetId,
        BigDecimal quantity) {
}