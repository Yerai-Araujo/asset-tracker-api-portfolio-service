package com.at.asset_tracker.portfolio.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.PortfolioEntity;

public interface PortfolioJpaRepository
        extends JpaRepository<PortfolioEntity, Long> {

}
