package com.sheoanna.teach_sphere.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private JwtService jwtService;
    private final String privateKeyPem = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCel8VDBuUdF9KDLjQvJH/gB2JmI9VRy/j4BbKrPVie1qMPa3wYdmj2VrGqYKwaekoLdDDWqmWUseVzqvm/Ps+xz1fNQtYIc/W9yTi5XjDxq4hFnXGkYPbtgbkw2ubm6o0+QYyQwx/lp50HRnAnacSMlgYvu+t6lhxW2kvdNpAIrl8nxM9Y79y0VCTmEFJ3Wt5Fs1v++9mXlj1lBJFt1czZVD/RLq9Fy86aFnmpDPU3qEmN7PTKYiwBYTZo7dQKVV0JyibgodSIeL88zSIrr1+YboiDM8htsnc7To4hUsLufP82O4xRXPbBVCcWj9tHkpNvYgalGoG5LY1+36/Cko1lAgMBAAECggEAFg7Xc7NjjgiYccDtJqpts7/rLngo7SPQ2NRYDauuZgdF7jjdrXxYrHyR4PfpsBQWtOGgAld4YfZdStl6u6AVnK5RUL4NTWcthIxWfKfOaGqafU5A9mM7LP0YQCSOLYttgj11OtQ83UETHOYt5rl2CjS6nwyE28vBnM3nc9jpLx+ze6QM7IiIqXvYAe7od8RRNDy66HgyIfG/2qsaWQhq0c8YyVU4a5nv004xdkkDeMstaWJ+14a4gBzShWrNkYxbozjUOGGZwNE8QCubvSxBmqpYop/JprImH+vWeHmOE53T6Aj503HKuvvyOVZYVlEfT4y9vcEdBaqwpcHNH/JZAQKBgQDV1JYbgbIMSeI7sXV5bxvOq2tZU14hNeeCKPka6bE9yOrmxE07f62X6CQpYw62pJbJYNCRou8s2SVPs2nrcjBqmvSjxczJBHNEJthqSEc8+El16gZ3hhEXqCh/09OYDoLKAzDjwM3UDBCFwv0KF7u8vK9gpNUpxDhEvlwo/ZSlAQKBgQC93nfTlM7cEEAd6bKzmrNYzd4uzM81to0VnR8Ao/SYGd8VbJC+Uq+8s7OJWW9/BwpVUvb8bSIDcBDbhH28whO2Nbl6ZIK9GvFDzbDP0py5KY0VUsqpEgtFqiMaXQbkkua8xMGD9yR/67kBOJiaz2Wq2IbQXfra2/pPP4PC7yl0ZQKBgQCfSKvEs7kg5EytMMUUf0rUieb+dFazIIdi8wiVHq3UG324B1SM1NBITznYxpxoO3TDb+YOBrRp0Ru1Ii3toHMmhmVkNe0VdMY0Z0dg7JR8e2uldX35vRmwaKg6iF2fcHfG6deaYL4yjgYkzhpWUVWdA2UU9dSAN1W/B5p5QmRfAQKBgHwZMzKseYQmbvHeNHnlrEFn1Ue7f9e4qIAvAPt3LkBX8JJdMkHjY4+v2LH2LcTWAjAAmQQmBGxAzBPehhxgsWePt9pCZZ1aRcUuZNmA7ASuHtOIGIPnEk0HRghOM1oklgFdjKpHV6jwliwxJLVSxz2iZoM8WeDAlNtFRUac8PhtAoGBAI46GlcdswBnTqOEzoOqQRHR+j0+NOYq9BN9vKKFxFTOTIgvQWGzLib5G3DdijjQi6r/+ildBn8Cv9WCTCgZmrbd8AOOBjT1/Bt/APidva62BPDsunaBo7RUbpVQKs8ZCg9ojso3QWfeYPlCzH8iqpJqnNA9qxSMfVJwJ7XGgh5M";
    private final String publicKeyPem = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnpfFQwblHRfSgy40LyR/4AdiZiPVUcv4+AWyqz1YntajD2t8GHZo9laxqmCsGnpKC3Qw1qpllLHlc6r5vz7Psc9XzULWCHP1vck4uV4w8auIRZ1xpGD27YG5MNrm5uqNPkGMkMMf5aedB0ZwJ2nEjJYGL7vrepYcVtpL3TaQCK5fJ8TPWO/ctFQk5hBSd1reRbNb/vvZl5Y9ZQSRbdXM2VQ/0S6vRcvOmhZ5qQz1N6hJjez0ymIsAWE2aO3UClVdCcom4KHUiHi/PM0iK69fmG6IgzPIbbJ3O06OIVLC7nz/NjuMUVz2wVQnFo/bR5KTb2IGpRqBuS2Nft+vwpKNZQIDAQAB";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "rsaPrivateKey", privateKeyPem);
        ReflectionTestUtils.setField(jwtService, "rsaPublicKey", publicKeyPem);
    }

    @Test
    void generateJwtToken_shouldReturnToken() {
        String username = "user1";
        String token = jwtService.generateJwtToken(username);

        assertNotNull(token);
        assertEquals("access_token", jwtService.extractTokenType(token));
        assertEquals(username, jwtService.extractSubject(token));
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void generateRefreshToken_shouldReturnToken() {
        String username = "user1";
        String token = jwtService.generateRefreshToken(username);

        assertNotNull(token);
        assertEquals("refresh_token", jwtService.extractTokenType(token));
        assertEquals(username, jwtService.extractSubject(token));
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void extractClaims_shouldReturnClaims() {
        String username = "user2";
        String token = jwtService.generateJwtToken(username);

        Claims claims = jwtService.extractClaims(token);

        assertEquals(username, claims.getSubject());
        assertEquals("access_token", claims.get("type"));
        assertNotNull(claims.getExpiration());
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() throws Exception {
        String expiredToken = Jwts.builder()
                .setSubject("user")
                .claim("type", "access_token")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(jwtService.generatePrivateKey())
                .compact();

        assertThrows(RuntimeException.class, () -> jwtService.validateToken(expiredToken));
    }
}