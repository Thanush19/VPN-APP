package com.safetunnel.backend.auth

import com.safetunnel.backend.users.User
import com.safetunnel.backend.users.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val authenticationManager: AuthenticationManager
) {

    fun register(email: String, rawPassword: String): AuthResponse {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }

        val user = User(
            email = email,
            passwordHash = passwordEncoder.encode(rawPassword),
            role = "ROLE_USER",
            status = "ACTIVE"
        )

        userRepository.save(user)
        return generateAuthResponse(user.email)
    }

    fun login(email: String, rawPassword: String): AuthResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, rawPassword)
        )

        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found")

        return generateAuthResponse(user.email)
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        val username = jwtUtils.getUsernameFromToken(refreshToken)
        if (!jwtUtils.validateToken(refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }
        return generateAuthResponse(username)
    }

    private fun generateAuthResponse(email: String): AuthResponse {
        val accessToken = jwtUtils.generateTokenFromUsername(email)
        val refreshToken = jwtUtils.generateRefreshToken(email)
        return AuthResponse(accessToken, refreshToken)
    }
}
