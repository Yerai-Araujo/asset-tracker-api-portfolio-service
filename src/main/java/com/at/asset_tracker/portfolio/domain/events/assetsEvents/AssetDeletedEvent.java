package com.at.asset_tracker.portfolio.domain.events.assetsEvents;

public record AssetDeletedEvent(Long assetId, String name) {
}
