package com.at.asset_tracker.portfolio.application.dto.response;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorResponse", description = "Standard error model")
public record ApiErrorResponse(
        @Schema(description = "Timestamp of the error") Instant timestamp,
        @Schema(description = "HTTP status code") int status,
        @Schema(description = "Error name") String error,
        @Schema(description = "Error details") String message,
        @Schema(description = "Endpoint path where error occurred") String path
) {}