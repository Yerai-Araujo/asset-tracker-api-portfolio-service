package com.at.asset_tracker.portfolio.infrastructure.persistence.repository.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.at.asset_tracker.portfolio.domain.model.Portfolio;
import com.at.asset_tracker.portfolio.domain.repository.PortfolioRepository;
import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.PortfolioEntity;
import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.PortfolioItemEntity;
import com.at.asset_tracker.portfolio.infrastructure.persistence.repository.PortfolioJpaRepository;

@Repository
public class PortfolioRepositoryImpl implements PortfolioRepository {

    private final PortfolioJpaRepository jpaRepository;

    public PortfolioRepositoryImpl(PortfolioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Portfolio save(Portfolio portfolio) {

        PortfolioEntity entity = toEntity(portfolio);
        PortfolioEntity saved = jpaRepository.save(entity);

        return toDomain(saved);
    }

    @Override
    public Optional<Portfolio> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    private PortfolioEntity toEntity(Portfolio portfolio) {

        PortfolioEntity entity = new PortfolioEntity();
        entity.setId(portfolio.id());

        if (portfolio.userId() != null) {
            entity.setUserId(portfolio.userId());
        }

        Set<PortfolioItemEntity> itemEntities = portfolio.items()
                .stream()
                .map(item -> {
                    PortfolioItemEntity itemEntity = new PortfolioItemEntity();
                    itemEntity.setId(item.id());
                    itemEntity.setAssetId(item.assetId());
                    itemEntity.setQuantity(item.quantity());
                    itemEntity.setPortfolio(entity);
                    return itemEntity;
                })
                .collect(Collectors.toSet());

        entity.setItems(itemEntities);

        return entity;
    }

    private Portfolio toDomain(PortfolioEntity entity) {

        Portfolio portfolio = new Portfolio(
                entity.getId(),
                entity.getUserId());

        entity.getItems().forEach(itemEntity -> {
            portfolio.internalAddItem(itemEntity.getId(),
                    itemEntity.getAssetId(),
                    itemEntity.getQuantity());
        });

        return portfolio;
    }

}
