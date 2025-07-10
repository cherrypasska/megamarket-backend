package backend.megamarket.orderservice;

import backend.megamarket.orderservice.entity.UserEntity;
import backend.megamarket.orderservice.entity.enums.Role;
import backend.megamarket.orderservice.services.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtServiceImpl();
        String testKey = "dGVzdHRlc3R0ZXN0dGVzdHRlc3R0ZXN0dGVzdHRlc3R0ZXN0"; // base64("testtesttesttesttesttesttesttest")
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", testKey);
    }

    @Test
    void generateAndExtractUsername_shouldWorkCorrectly() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setId(123L);
        user.setEmail("test@example.com");
        user.setRole(Role.ROLE_ADMIN);

        String token = jwtService.generateToken(user);
        String username = jwtService.extractUserName(token);

        assertEquals("testuser", username);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        UserEntity user = new UserEntity();
        user.setUsername("validuser");
        user.setId(1L);
        user.setEmail("valid@example.com");
        user.setRole(Role.ROLE_USER);

        String token = jwtService.generateToken(user);
        boolean valid = jwtService.isTokenValid(token, user);

        assertTrue(valid);
    }

    @Test
    void isTokenExpired_shouldReturnFalseForNewToken() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setId(1L);
        user.setEmail("email@example.com");
        user.setRole(Role.ROLE_USER);

        String token = jwtService.generateToken(user);
        boolean expired = jwtService.isTokenExpired(token);

        assertFalse(expired);
    }

    @Test
    void extractExpiration_shouldReturnFutureDate() {
        UserEntity user = new UserEntity();
        user.setUsername("expuser");
        user.setId(1L);
        user.setEmail("exp@example.com");
        user.setRole(Role.ROLE_USER);

        String token = jwtService.generateToken(user);
        Date expiration = jwtService.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }
}
