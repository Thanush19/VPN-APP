package com.safetunnel.backend.common.config

import com.safetunnel.backend.common.exception.DomainException
import com.safetunnel.backend.common.exception.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler(private val environment: Environment) : ResponseEntityExceptionHandler() {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: org.springframework.http.HttpHeaders,
        status: org.springframework.http.HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("Validation error: ${ex.message}")
        val errors = ex.allErrors
            .map { it as DefaultMessageSourceResolvable }
            .map { it.defaultMessage }
            .toList()

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.message
        )
        problemDetail.title = "Validation Error"
        problemDetail.setProperty("errors", errors)
        problemDetail.setProperty("timestamp", Instant.now())

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail)
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ProblemDetail {
        logger.info("Domain exception: ${ex.message}")
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.message
        )
        problemDetail.title = "Bad Request"
        problemDetail.setProperty("errors", listOf(ex.message))
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ProblemDetail {
        logger.error("Resource not found: ${ex.message}")
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.message
        )
        problemDetail.title = "Resource Not Found"
        problemDetail.setProperty("errors", listOf(ex.message))
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(ex: Exception): ProblemDetail {
        logger.error("Unexpected exception occurred", ex)

        // Don't expose internal details in production
        val message = if (isDevelopmentMode()) {
            ex.message ?: "An unexpected error occurred"
        } else {
            "An unexpected error occurred"
        }

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            message
        )
        problemDetail.title = "Internal Server Error"
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

    private fun isDevelopmentMode(): Boolean {
        val profiles = environment.activeProfiles.toList()
        return profiles.contains("dev") || profiles.contains("local") || profiles.contains("test")
    }
}