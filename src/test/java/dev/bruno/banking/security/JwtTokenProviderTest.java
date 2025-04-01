package dev.bruno.banking.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET_KEY = "5f4e1d7b87e3df44e3e72f6ecf87611b8c5bc477a50cf914a786a980d09bdb7c";
    private static final long EXPIRATION_TIME = 10000; // 10 segundos

    private JwtTokenProvider jwtTokenProvider;
    private String token;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, EXPIRATION_TIME);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new User("testuser", "password", Collections.emptyList()),
                null
        );
        token = jwtTokenProvider.generateToken(authentication);
    }

    @Test
    void testGenerateTokenAndGetUsername() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
        String username = jwtTokenProvider.getUsernameFromJWT(token);
        assertEquals("testuser", username);
    }

    @Test
    void testValidateToken_Valid() {
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testValidateToken_Null() {
        assertFalse(jwtTokenProvider.validateToken(null));
    }


    @Test
    void testValidateToken_Expired() throws InterruptedException {
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(SECRET_KEY, 1);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new User("testuser", "password", Collections.emptyList()),
                null
        );
        String shortToken = shortLivedProvider.generateToken(authentication);
        Thread.sleep(10);
        assertFalse(shortLivedProvider.validateToken(shortToken));
    }

    @Test
    void testGetUsernameFromJWT_InvalidToken() {
        String invalidToken = "invalid.token.string";
        Exception ex = assertThrows(Exception.class, () -> jwtTokenProvider.getUsernameFromJWT(invalidToken));
        assertNotNull(ex.getMessage());
    }

    @Test
    void testConstructor_InvalidSecret() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> new JwtTokenProvider("  ", EXPIRATION_TIME));
        assertEquals("A chave secreta JWT não foi configurada corretamente.", exception.getMessage());
    }
}
