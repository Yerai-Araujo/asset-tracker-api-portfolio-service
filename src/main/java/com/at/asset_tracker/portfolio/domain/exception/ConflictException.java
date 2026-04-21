package com.at.asset_tracker.portfolio.domain.exception;


public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
