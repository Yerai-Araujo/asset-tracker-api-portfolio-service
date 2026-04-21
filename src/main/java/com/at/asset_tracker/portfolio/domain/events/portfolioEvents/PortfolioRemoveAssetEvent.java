package com.at.asset_tracker.portfolio.domain.events.portfolioEvents;

public record PortfolioRemoveAssetEvent(Long portfolioId, Long assetId) {
    
}
