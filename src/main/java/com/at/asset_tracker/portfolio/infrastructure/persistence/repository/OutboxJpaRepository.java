package com.at.asset_tracker.portfolio.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.OutboxEvent;

public interface OutboxJpaRepository
        extends JpaRepository<OutboxEvent, UUID> {

}