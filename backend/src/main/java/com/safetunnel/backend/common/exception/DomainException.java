package com.safetunnel.backend.common.exception;

/**
 * Base exception for domain-specific errors.
 * These represent business rule violations.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}