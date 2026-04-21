package com.at.asset_tracker.portfolio.domain.repository;

import java.util.Optional;

import com.at.asset_tracker.portfolio.domain.model.Asset;

public interface AssetRepository {

    Asset save(Asset asset);

    boolean existsBySymbol(String symbol);

    Optional<Asset> findById(Long id);

    void delete(Long id);

}


