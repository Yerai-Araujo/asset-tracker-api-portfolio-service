package com.at.asset_tracker.portfolio.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at.asset_tracker.portfolio.domain.events.assetsEvents.AssetCreatedEvent;
import com.at.asset_tracker.portfolio.domain.events.assetsEvents.AssetDeletedEvent;
import com.at.asset_tracker.portfolio.domain.model.Asset;
import com.at.asset_tracker.portfolio.domain.model.enums.AssetType;
import com.at.asset_tracker.portfolio.domain.model.enums.AssetUnit;
import com.at.asset_tracker.portfolio.domain.repository.AssetRepository;
import com.at.asset_tracker.portfolio.domain.repository.OutboxRepository;
import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.OutboxEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class AssetApplicationService {

    private final AssetRepository assetRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public AssetApplicationService(AssetRepository assetRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.assetRepository = assetRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public Asset create(String symbol, AssetType type, AssetUnit unit, String name) {

        if (assetRepository.existsBySymbol(symbol)) {
            throw new IllegalStateException("Asset already exists");
        }

        Asset asset = new Asset(null, symbol, type, unit, name);
        Asset saved = assetRepository.save(asset);

        AssetCreatedEvent event = new AssetCreatedEvent(saved.id(), saved.name());
        JsonNode payload = objectMapper.valueToTree(event);

        OutboxEvent outboxEvent = OutboxEvent.assetCreated(saved.id(), payload);
        outboxRepository.save(outboxEvent);

        return saved;
    }

    @Transactional(readOnly = true)
    public Asset findById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
    }

    @Transactional(readOnly = true)
    public boolean existsBySymbol(String symbol) {
        return assetRepository.existsBySymbol(symbol);
    }

    public void delete(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));

        assetRepository.delete(id);
        AssetDeletedEvent event = new AssetDeletedEvent(asset.id(), asset.name());
        JsonNode payload = objectMapper.valueToTree(event);

        OutboxEvent outboxEvent = OutboxEvent.assetDeleted(asset.id(), payload);
        outboxRepository.save(outboxEvent);
    }
}
