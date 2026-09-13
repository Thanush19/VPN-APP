package com.safetunnel.backend.common.exception

/**
 * Base exception for domain-specific errors.
 * These represent business rule violations.
 */
open class DomainException(message: String) : RuntimeException(message)