package com.at.asset_tracker.portfolio.domain.events.portfolioEvents;

public record PortfolioCreatedEvent(
        Long portfolioId,
        Long userId) {
}
