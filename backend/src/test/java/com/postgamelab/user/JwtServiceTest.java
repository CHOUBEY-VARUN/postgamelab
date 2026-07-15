package com.postgamelab.user;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    @Test
    void generatedTokenIsSignedAndExpiresAfterConfiguredDuration() {
        String encodedSecret = Base64.getEncoder().encodeToString(
                "0123456789abcdef0123456789abcdef"
                        .getBytes(StandardCharsets.UTF_8)
        );
        Duration expiration = Duration.ofHours(1);
        UUID userId = UUID.randomUUID();

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        JwtService jwtService = new JwtService(
                encodedSecret,
                expiration
        );

        String token = jwtService.generateToken(user);

        SecretKey verificationKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(encodedSecret)
        );

        Claims claims = Jwts.parser()
                .verifyWith(verificationKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(userId.toString(), claims.getSubject());
        assertEquals(
                expiration.toSeconds(),
                Duration.between(
                        claims.getIssuedAt().toInstant(),
                        claims.getExpiration().toInstant()
                ).toSeconds()
        );
    }
}