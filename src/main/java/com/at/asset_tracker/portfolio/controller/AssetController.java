package com.at.asset_tracker.portfolio.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.at.asset_tracker.portfolio.application.dto.request.CreateAssetRequest;
import com.at.asset_tracker.portfolio.application.dto.response.ApiErrorResponse;
import com.at.asset_tracker.portfolio.application.dto.response.AssetResponse;
import com.at.asset_tracker.portfolio.application.service.AssetApplicationService;
import com.at.asset_tracker.portfolio.domain.annotations.StandardApiErrors;
import com.at.asset_tracker.portfolio.domain.model.Asset;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/assets")
@StandardApiErrors
public class AssetController {

    private final AssetApplicationService assetService;

    public AssetController(AssetApplicationService assetService) {
        this.assetService = assetService;
    }

    @Operation(summary = "Create an asset")
    @ApiResponse(responseCode = "201", description = "Asset created")
    @ApiResponse(responseCode = "409", description = "Asset already exists", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    public ResponseEntity<AssetResponse> create(@RequestBody CreateAssetRequest request) {

        Asset asset = assetService.create(
                request.symbol(),
                request.type(),
                request.unit(),
                request.name()
        );

        return ResponseEntity
        .created(URI.create("/api/assets/" + asset.id()))
        .body(toResponse(asset));
    }

    @Operation(summary = "Get asset by ID")
    @ApiResponse(responseCode = "200", description = "Asset found")
    @ApiResponse(responseCode = "404", description = "Asset not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> findById(@PathVariable Long id) {

        Asset asset = assetService.findById(id);

        return ResponseEntity.ok(toResponse(asset));
    }

    @Operation(summary = "Delete an asset")
    @ApiResponse(responseCode = "204", description = "Asset deleted")
    @ApiResponse(responseCode = "404", description = "Asset not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { 

        assetService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private AssetResponse toResponse(Asset asset) {
        return new AssetResponse(
                asset.id(),
                asset.symbol(),
                asset.type(),
                asset.unit(),
                asset.name()
        );
    }
}
