package com.at.asset_tracker.portfolio.infrastructure.persistence.repository.impl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.at.asset_tracker.portfolio.domain.model.Asset;
import com.at.asset_tracker.portfolio.domain.repository.AssetRepository;
import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.AssetEntity;
import com.at.asset_tracker.portfolio.infrastructure.persistence.repository.AssetJpaRepository;

@Repository
public class AssetRepositoryImpl implements AssetRepository {

    private final AssetJpaRepository jpaRepository;

    public AssetRepositoryImpl(AssetJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Asset save(Asset asset) {

        AssetEntity entity = toEntity(asset);

        AssetEntity saved = jpaRepository.save(entity);

        return toDomain(saved);
    }

    @Override
    public boolean existsBySymbol(String symbol) {
        return jpaRepository.existsBySymbol(symbol);
    }

    private AssetEntity toEntity(Asset asset) {
        AssetEntity entity = new AssetEntity();
        entity.setId(asset.id());
        entity.setSymbol(asset.symbol());
        entity.setType(asset.type());
        entity.setUnit(asset.unit());
        entity.setName(asset.name());
        return entity;
    }

    private Asset toDomain(AssetEntity entity) {
        return new Asset(
                entity.getId(),
                entity.getSymbol(),
                entity.getType(),
                entity.getUnit(),
                entity.getName()
        );
    }

    @Override
    public Optional<Asset> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

}
