package com.at.asset_tracker.portfolio.domain.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Portfolio {

    private Long id;

    private Long userId;

    private Set<PortfolioItem> items = new HashSet<>();

    public Portfolio(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long id() {
        return id;
    }

    public Long userId() {
        return userId;
    }

    public Set<PortfolioItem> items() {
        return Collections.unmodifiableSet(items);
    }

    public static Portfolio create(Long userId) {
        return new Portfolio(null, userId);   
    }

    public void addAsset(Long assetId, BigDecimal quantity) {

        if (quantity.signum() <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        PortfolioItem item = items.stream()
                .filter(i -> i.assetId().equals(assetId))
                .findFirst()
                .orElseGet(() -> {
                    PortfolioItem newItem = new PortfolioItem(null, assetId, BigDecimal.ZERO);
                    items.add(newItem);
                    return newItem;
                });

        item.increaseQuantity(quantity);
    }

    public void removeAsset(Long assetId) {
        items.removeIf(i -> i.assetId().equals(assetId));
    }

    public void internalAddItem(Long id, Long assetId, BigDecimal quantity) {
        PortfolioItem item = new PortfolioItem(id, assetId, quantity);
        items.add(item);
    }

}
