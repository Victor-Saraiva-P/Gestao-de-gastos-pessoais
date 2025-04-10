package br.com.gestorfinanceiro;

import org.junit.jupiter.api.Test;

import br.com.gestorfinanceiro.config.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String secret = "secretKeysecretKeysecretKeysecretKeysecretKeysecretKey";
    private final Long expiration = 3600000L;
    
    private String validToken;
    private String expiredToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
        
        // Token válido
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "user@example.com");
        claims.put("role", "USER");
        validToken = Jwts.builder()
                .setClaims(claims)
                .setSubject("username123")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // Testes de integração
    @Test
    void extractUsername_ShouldReturnUsername_WhenTokenIsValid() {
        String username = jwtUtil.extractUsername(validToken);
        assertEquals("username123", username);
    }

    @Test
    void extractUsername_ShouldThrowException_WhenTokenIsInvalid() {
        assertThrows(Exception.class, () -> jwtUtil.extractUsername("invalid.token.string"));
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValidAndEmailMatches() {
        assertTrue(jwtUtil.validateToken(validToken, "user@example.com"));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenEmailDoesNotMatch() {
        assertFalse(jwtUtil.validateToken(validToken, "wrong@example.com"));
    }
}