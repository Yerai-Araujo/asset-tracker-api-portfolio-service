package com.at.asset_tracker.portfolio.application.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.at.asset_tracker.portfolio.application.dto.response.PriceResponse;
import com.at.asset_tracker.portfolio.domain.model.Portfolio;
import com.at.asset_tracker.portfolio.domain.repository.AssetRepository;
import com.at.asset_tracker.portfolio.domain.repository.PortfolioRepository;

@Service
@Transactional
public class PortfolioApplicationService {

    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final WebClient marketWebClient;

    public PortfolioApplicationService(PortfolioRepository portfolioRepository,
            AssetRepository assetRepository, @Qualifier("marketWebClient") WebClient marketWebClient) {
        this.portfolioRepository = portfolioRepository;
        this.marketWebClient = marketWebClient;
        this.assetRepository = assetRepository;
    }

    public Portfolio create(Long userId) {
        Portfolio portfolio = new Portfolio(null, userId);
        return portfolioRepository.save(portfolio);
    }

    public Portfolio addAsset(Long portfolioId, Long assetId, BigDecimal quantity) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

        portfolio.addAsset(assetId, quantity);

        return portfolioRepository.save(portfolio);
    }

    @Transactional(readOnly = true)
    public Portfolio findById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
    }

    @Transactional(readOnly = true)
    public BigDecimal calculatePortfolioValue(Long portfolioId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        return portfolio.items()
                .stream()
                .map(item -> {

                    var asset = assetRepository.findById(item.assetId())
                            .orElseThrow(() -> new RuntimeException("Asset not found"));

                    BigDecimal price = getCurrentPrice(asset.symbol(), asset.type().name());

                    return price.multiply(item.quantity());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getCurrentPrice(String symbol, String type) {

        return marketWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/prices")
                        .queryParam("symbol", symbol)
                        .queryParam("type", type)
                        .build())
                .retrieve()
                .bodyToMono(PriceResponse.class)
                .block()
                .price();
    }

}
