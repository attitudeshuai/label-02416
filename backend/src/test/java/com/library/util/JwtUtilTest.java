package com.library.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void generateToken_Success() {
        String token = jwtUtil.generateToken(1L, "testuser", 0);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void verifyToken_Success() {
        String token = jwtUtil.generateToken(1L, "testuser", 0);

        DecodedJWT decoded = jwtUtil.verifyToken(token);

        assertNotNull(decoded);
        assertEquals("testuser", decoded.getClaim("username").asString());
    }

    @Test
    void getUserId_Success() {
        String token = jwtUtil.generateToken(1L, "testuser", 0);

        Long userId = jwtUtil.getUserId(token);

        assertEquals(1L, userId);
    }

    @Test
    void getUsername_Success() {
        String token = jwtUtil.generateToken(1L, "testuser", 0);

        String username = jwtUtil.getUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void getRole_Success() {
        String token = jwtUtil.generateToken(1L, "testuser", 1);

        Integer role = jwtUtil.getRole(token);

        assertEquals(1, role);
    }

    @Test
    void verifyToken_InvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.verifyToken("invalid-token"));
    }
}
