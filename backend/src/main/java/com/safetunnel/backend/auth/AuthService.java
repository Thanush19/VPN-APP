package com.safetunnel.backend.auth;

import com.safetunnel.backend.users.User;
import com.safetunnel.backend.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER");
        user.setStatus("ACTIVE");

        userRepository.save(user);
        return generateAuthResponse(user.getEmail());
    }

    public AuthResponse login(String email, String rawPassword) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );

        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return generateAuthResponse(user.getEmail());
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        return generateAuthResponse(username);
    }

    private AuthResponse generateAuthResponse(String email) {
        String accessToken = jwtUtils.generateTokenFromUsername(email);
        String refreshToken = jwtUtils.generateRefreshToken(email);
        return new AuthResponse(accessToken, refreshToken);
    }
}