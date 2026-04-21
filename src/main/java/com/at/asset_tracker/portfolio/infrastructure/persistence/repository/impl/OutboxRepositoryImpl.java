package com.at.asset_tracker.portfolio.infrastructure.persistence.repository.impl;

import org.springframework.stereotype.Repository;

import com.at.asset_tracker.portfolio.domain.repository.OutboxRepository;
import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.OutboxEvent;
import com.at.asset_tracker.portfolio.infrastructure.persistence.repository.OutboxJpaRepository;

@Repository
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository jpaRepository;

    public OutboxRepositoryImpl(OutboxJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public void save(OutboxEvent event) {
        jpaRepository.save(event);
    }

}
