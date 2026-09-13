package com.safetunnel.backend.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtils(
    @Value("\${jwt.secret:safetunnelsecretkeymustbeverylongandsecureforhmacsha256algorithm}")
    private val jwtSecret: String,
    @Value("\${jwt.expiration:86400000}") // 24 hours
    private val jwtExpirationMs: Long,
    @Value("\${jwt.refresh-expiration:604800000}") // 7 days
    private val refreshExpirationMs: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun generateToken(userDetails: UserDetails): String {
        return generateTokenFromUsername(userDetails.username)
    }

    fun generateTokenFromUsername(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshExpirationMs)

        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        val claims: Claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (e: Exception) {
            // Log or handle token validation error
        }
        return false
    }
}
