package com.at.asset_tracker.portfolio.domain.events.assetsEvents;

public record AssetCreatedEvent(
        Long assetId,
        String name) {
}