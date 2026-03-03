package com.at.asset_tracker.portfolio.infrastructure.persistence.dataLoader;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.at.asset_tracker.portfolio.domain.model.Asset;
import com.at.asset_tracker.portfolio.domain.model.enums.AssetType;
import com.at.asset_tracker.portfolio.domain.model.enums.AssetUnit;
import com.at.asset_tracker.portfolio.domain.repository.AssetRepository;

@Component
public class AssetDataLoader implements ApplicationRunner {

    private final AssetRepository repository;

    public AssetDataLoader(AssetRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (!repository.existsBySymbol("BTC")) {
            repository.save(new Asset(null, "BTC", AssetType.CRYPTO, AssetUnit.BTC, "Bitcoin"));
        }

        if (!repository.existsBySymbol("XAG")) {
            repository.save(new Asset(null, "XAG", AssetType.METAL, AssetUnit.OUNCE, "Plata"));
        }

        if (!repository.existsBySymbol("XAU")) {
            repository.save(new Asset(null, "XAU", AssetType.METAL, AssetUnit.OUNCE, "Oro"));
        }
    }
}