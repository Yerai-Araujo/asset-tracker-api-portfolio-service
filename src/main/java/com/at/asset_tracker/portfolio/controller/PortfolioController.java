package com.at.asset_tracker.portfolio.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.at.asset_tracker.portfolio.application.dto.request.AddPortfolioItemRequest;
import com.at.asset_tracker.portfolio.application.dto.request.CreatePortfolioRequest;
import com.at.asset_tracker.portfolio.application.dto.response.ApiErrorResponse;
import com.at.asset_tracker.portfolio.application.dto.response.PortfolioItemResponse;
import com.at.asset_tracker.portfolio.application.dto.response.PortfolioResponse;
import com.at.asset_tracker.portfolio.application.dto.response.UserResponse;
import com.at.asset_tracker.portfolio.application.service.PortfolioApplicationService;
import com.at.asset_tracker.portfolio.application.service.UserApplicationServiceClient;
import com.at.asset_tracker.portfolio.domain.annotations.StandardApiErrors;
import com.at.asset_tracker.portfolio.domain.model.Portfolio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/portfolios")
@StandardApiErrors
public class PortfolioController {

    private final PortfolioApplicationService portfolioService;
    private final UserApplicationServiceClient userClient;

    public PortfolioController(PortfolioApplicationService portfolioService, UserApplicationServiceClient userClient) {
        this.portfolioService = portfolioService;
        this.userClient = userClient;
    }

    @Operation(summary = "Create a portfolio")
    @ApiResponse(responseCode = "201", description = "Portfolio created")
    @ApiResponse(responseCode = "409", description = "Portfolio already exists", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    public ResponseEntity<PortfolioResponse> create(@RequestBody CreatePortfolioRequest request) {

        Portfolio portfolio = portfolioService.create(request.userId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(portfolio.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(toResponse(portfolio));
    }

    @Operation(summary = "Find a portfolio by ID")
    @ApiResponse(responseCode = "200", description = "Portfolio found")
    @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> findById(@PathVariable Long id) {

        Portfolio portfolio = portfolioService.findById(id);

        return ResponseEntity.ok(toResponse(portfolio));
    }

    @Operation(summary = "Calculate the total value of a portfolio")
    @ApiResponse(responseCode = "200", description = "Value calculated successfully")
    @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "502", description = "Error from external pricing service")
    @ApiResponse(responseCode = "503", description = "External service unavailable")
    @GetMapping("/{id}/value")
    public ResponseEntity<BigDecimal> calculateValue(@PathVariable Long id) {

        BigDecimal value = portfolioService.calculatePortfolioValue(id);

        return ResponseEntity.ok(value);
    }

    @Operation(summary = "Add an asset to a portfolio")
    @ApiResponse(responseCode = "201", description = "Asset added to portfolio successfully", content = @Content(schema = @Schema(implementation = PortfolioResponse.class)))
    @ApiResponse(responseCode = "404", description = "Portfolio or asset not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Asset already exists in portfolio", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping("/{id}/items")
    public ResponseEntity<PortfolioResponse> addAsset(
            @PathVariable Long id,
            @Valid @RequestBody AddPortfolioItemRequest request) {

        Portfolio portfolio = portfolioService.addAsset(id, request.assetId(), request.quantity());

        return ResponseEntity
                .created(URI.create("/api/portfolios/" + id + "/items/" + request.assetId()))
                .body(toResponse(portfolio));
    }

    @Operation(summary = "Check if a user exists by ID")
    @ApiResponse(responseCode = "200", description = "User exists")
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/{id}/exists")
    public ResponseEntity<UserResponse> userExists(@PathVariable Long id) {

        UserResponse user = userClient.userExists(id);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Delete a portfolio by ID")
    @ApiResponse(responseCode = "204", description = "Portfolio deleted successfully")
    @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        portfolioService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // @Operation(summary = "Update the quantity of an asset in a portfolio")
    // @ApiResponse(responseCode = "200", description = "Asset quantity updated successfully", content = @Content(schema = @Schema(implementation = PortfolioResponse.class)))
    // @ApiResponse(responseCode = "404", description = "Portfolio or asset not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    // @ApiResponse(responseCode = "409", description = "Asset not found in portfolio", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    // @PutMapping("/{id}/items/{assetId}")
    // public ResponseEntity<PortfolioResponse> updateAssetQuantity(
    //         @PathVariable Long id,
    //         @PathVariable Long assetId,
    //         @Valid @RequestBody AddPortfolioItemRequest request) {

    //     Portfolio portfolio = portfolioService.updateAssetQuantity(id, assetId, request.quantity());

    //     return ResponseEntity.ok(toResponse(portfolio));
    // }

    @Operation(summary = "Remove an asset from a portfolio")
    @ApiResponse(responseCode = "204", description = "Asset removed from portfolio successfully")
    @ApiResponse(responseCode = "404", description = "Portfolio or asset not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Asset not found in portfolio", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PutMapping("/{id}/items/{assetId}")
    public ResponseEntity<Void> removeAsset(
            @PathVariable Long id,
            @PathVariable Long assetId) { 

        portfolioService.removeAsset(id, assetId);
        return ResponseEntity.noContent().build();
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
