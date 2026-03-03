package com.at.asset_tracker.portfolio.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.at.asset_tracker.portfolio.application.dto.request.AddPortfolioItemRequest;
import com.at.asset_tracker.portfolio.application.dto.request.CreatePortfolioRequest;
import com.at.asset_tracker.portfolio.application.dto.response.PortfolioItemResponse;
import com.at.asset_tracker.portfolio.application.dto.response.PortfolioResponse;
import com.at.asset_tracker.portfolio.application.service.PortfolioApplicationService;
import com.at.asset_tracker.portfolio.domain.model.Portfolio;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioApplicationService portfolioService;

    public PortfolioController(PortfolioApplicationService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    public ResponseEntity<PortfolioResponse> create(@RequestBody CreatePortfolioRequest request) {

        Portfolio portfolio = portfolioService.create(request.userId());

        return ResponseEntity
                .created(URI.create("/api/portfolios/" + portfolio.id()))
                .body(toResponse(portfolio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> findById(@PathVariable Long id) {

        Portfolio portfolio = portfolioService.findById(id);

        return ResponseEntity.ok(toResponse(portfolio));
    }

    @GetMapping("/{id}/value")
    public ResponseEntity<BigDecimal> calculateValue(@PathVariable Long id) {

        BigDecimal value = portfolioService.calculatePortfolioValue(id);

        return ResponseEntity.ok(value);
    }

    @PostMapping("/{id}/addAsset")
    public ResponseEntity<PortfolioResponse> addAsset(@PathVariable Long id, @RequestBody AddPortfolioItemRequest request) {

        Portfolio portfolio = portfolioService.addAsset(id, request.assetId(), request.quantity());

        return ResponseEntity
                .created(URI.create("/api/portfolios/" + id + "/addAsset/" + request.assetId()))
                .body(toResponse(portfolio));
    }

    private PortfolioResponse toResponse(Portfolio portfolio) {

        Set<PortfolioItemResponse> items = portfolio.items()
                .stream()
                .map(item -> new PortfolioItemResponse(
                        item.id(),
                        item.assetId(),
                        item.quantity()))
                .collect(Collectors.toSet());

        return new PortfolioResponse(
                portfolio.id(),
                portfolio.userId(),
                items);
    }
}
