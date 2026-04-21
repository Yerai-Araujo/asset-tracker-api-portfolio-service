package com.at.asset_tracker.portfolio.domain.repository;

import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.OutboxEvent;

public interface OutboxRepository {

    void save(OutboxEvent event);
}
