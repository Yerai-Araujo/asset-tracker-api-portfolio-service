package com.at.asset_tracker.portfolio.domain.repository;

import java.util.Optional;

import com.at.asset_tracker.portfolio.domain.model.Portfolio;


public interface PortfolioRepository {

    Portfolio save(Portfolio portfolio);

    Optional<Portfolio> findById(Long id);
}

