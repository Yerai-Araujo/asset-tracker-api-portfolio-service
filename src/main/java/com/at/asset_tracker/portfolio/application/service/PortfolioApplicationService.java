package com.at.asset_tracker.portfolio.application.service;

import java.math.BigDecimal;

import javax.sound.sampled.Port;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.at.asset_tracker.portfolio.application.dto.response.PriceResponse;
import com.at.asset_tracker.portfolio.domain.events.marketWebEvents.AssetsValueCalculatedEvent;
import com.at.asset_tracker.portfolio.domain.events.portfolioEvents.PortfolioAddAssetEvent;
import com.at.asset_tracker.portfolio.domain.events.portfolioEvents.PortfolioCreatedEvent;
import com.at.asset_tracker.portfolio.domain.events.portfolioEvents.PortfolioDeletedEvent;
import com.at.asset_tracker.portfolio.domain.events.portfolioEvents.PortfolioRemoveAssetEvent;
import com.at.asset_tracker.portfolio.domain.events.portfolioEvents.PortfolioUpdateAssetEvent;
import com.at.asset_tracker.portfolio.domain.exception.BadRequestException;
import com.at.asset_tracker.portfolio.domain.exception.ConflictException;
import com.at.asset_tracker.portfolio.domain.exception.ResourceNotFoundException;
import com.at.asset_tracker.portfolio.domain.model.Portfolio;
import com.at.asset_tracker.portfolio.domain.repository.AssetRepository;
import com.at.asset_tracker.portfolio.domain.repository.OutboxRepository;
import com.at.asset_tracker.portfolio.domain.repository.PortfolioRepository;
import com.at.asset_tracker.portfolio.infrastructure.persistence.entity.OutboxEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class PortfolioApplicationService {

    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final WebClient marketWebClient;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PortfolioApplicationService(PortfolioRepository portfolioRepository,
            AssetRepository assetRepository, @Qualifier("marketWebClient") WebClient marketWebClient,
            OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.portfolioRepository = portfolioRepository;
        this.marketWebClient = marketWebClient;
        this.assetRepository = assetRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public Portfolio create(Long userId) {
        if(userId == null) {
            throw new BadRequestException("User ID cannot be null");
        }
        if(existsByUserId(userId)) {
            throw new ConflictException("Portfolio for user " + userId + " already exists");
        }

        Portfolio portfolio = Portfolio.create(userId);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        PortfolioCreatedEvent event = new PortfolioCreatedEvent(savedPortfolio.id(), savedPortfolio.userId());
        JsonNode payload = objectMapper.valueToTree(event);

        OutboxEvent outboxEvent = OutboxEvent.portfolioCreated(savedPortfolio.id(), payload);
        outboxRepository.save(outboxEvent);

        return savedPortfolio;
    }

    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        return portfolioRepository.existsByUserId(userId);
    }

    public Portfolio addAsset(Long portfolioId, Long assetId, BigDecimal quantity) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        portfolio.addAsset(assetId, quantity);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        PortfolioAddAssetEvent event = new PortfolioAddAssetEvent(savedPortfolio.id(), assetId, quantity);
        JsonNode payload = objectMapper.valueToTree(event);
        OutboxEvent outboxEvent = OutboxEvent.portfolioItemAdded(savedPortfolio.id(), payload);
        outboxRepository.save(outboxEvent);

        return savedPortfolio;
    }

    @Transactional(readOnly = true)
    public Portfolio findById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
    }

    public BigDecimal calculatePortfolioValue(Long portfolioId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        BigDecimal calculatedValue = portfolio.items()
                .stream()
                .map(item -> {

                    var asset = assetRepository.findById(item.assetId())
                            .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

                    BigDecimal price = getCurrentPrice(asset.symbol(), asset.type().name());

                    return price.multiply(item.quantity());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        AssetsValueCalculatedEvent event = new AssetsValueCalculatedEvent(portfolioId, calculatedValue);
        JsonNode payload = objectMapper.valueToTree(event);
        OutboxEvent outboxEvent = OutboxEvent.assetsValueCalculated(portfolioId, payload);
        outboxRepository.save(outboxEvent);
            
        return calculatedValue;
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

    public Portfolio updateAssetQuantity(Long portfolioId, Long assetId, BigDecimal quantity) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        portfolio.addAsset(assetId, quantity);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        PortfolioUpdateAssetEvent event = new PortfolioUpdateAssetEvent(savedPortfolio.id(), assetId, quantity);
        JsonNode payload = objectMapper.valueToTree(event);
        OutboxEvent outboxEvent = OutboxEvent.portfolioItemUpdated(savedPortfolio.id(), payload);
        outboxRepository.save(outboxEvent);

        return savedPortfolio;
    }

    public void delete(Long portfolioId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        portfolioRepository.deleteById(portfolioId);

        JsonNode payload = objectMapper.valueToTree(new PortfolioDeletedEvent(portfolioId, portfolio.userId()));
        OutboxEvent outboxEvent = OutboxEvent.portfolioDeleted(portfolioId, payload);
        outboxRepository.save(outboxEvent);
    }

    public void removeAsset(Long portfolioId, Long assetId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        portfolio.removeAsset(assetId);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        PortfolioRemoveAssetEvent event = new PortfolioRemoveAssetEvent(savedPortfolio.id(), assetId);
        JsonNode payload = objectMapper.valueToTree(event);
        OutboxEvent outboxEvent = OutboxEvent.portfolioItemRemoved(savedPortfolio.id(), payload);
        outboxRepository.save(outboxEvent);
    }
    
}
