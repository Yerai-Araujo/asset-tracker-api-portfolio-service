package com.at.asset_tracker.portfolio.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(name = "aggregateid", nullable = false)
    private String aggregateId;

    @Column(name = "aggregatetype", nullable = false)
    private String aggregateType;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode payload;

    @Column(name = "createdat", nullable = false)
    private Instant createdAt;

    public OutboxEvent() {
    }

    public OutboxEvent(String aggregateId, String aggregateType, String type, JsonNode payload) {
        this.id = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.type = type;
        this.payload = payload;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public static OutboxEvent portfolioCreated(Long aggregateId, JsonNode payload) {
        return new OutboxEvent(aggregateId.toString(), "Portfolio", "PortfolioCreated", payload);
    }

    public static OutboxEvent portfolioItemAdded(Long aggregateId, JsonNode payload) {
        return new OutboxEvent(aggregateId.toString(), "Portfolio", "PortfolioItemAdded", payload);
    }

    public static OutboxEvent portfolioItemRemoved(Long aggregateId, JsonNode payload) {
        return new OutboxEvent(aggregateId.toString(), "Portfolio", "PortfolioItemRemoved", payload);
    }

    public static OutboxEvent portfolioDeleted(Long aggregateId, JsonNode payload) {
        return new OutboxEvent(aggregateId.toString(), "Portfolio", "PortfolioDeleted", payload);
    }

    public static OutboxEvent portfolioItemUpdated(Long aggregateId, JsonNode payload) {
        return new OutboxEvent(aggregateId.toString(), "Portfolio", "PortfolioItemUpdated", payload);
    }

    public static OutboxEvent assetsValueCalculated(Long aggregateId, JsonNode payload) {
        return new OutboxEvent(aggregateId.toString(), "Portfolio", "AssetsValueCalculated", payload);
    }

    public static OutboxEvent assetCreated(Long aggregateId, JsonNode payload) {
        return new OutboxEvent(aggregateId.toString(), "Asset", "AssetCreated", payload);
    }

    public static OutboxEvent assetDeleted(Long aggregateId, JsonNode payload) {
        return new OutboxEvent(aggregateId.toString(), "Asset", "AssetDeleted", payload);
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getType() {
        return type;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}