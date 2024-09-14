package org.mounanga.securityservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mounanga.securityservice.entity.Profile;
import org.mounanga.securityservice.entity.Role;
import org.mounanga.securityservice.entity.User;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    private JwtService jwtService;

    User user;
    String secret;
    Long jwtExpiration;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        secret = "secretABCHSDIDKZHDKDLSJDJDNQ";
        jwtExpiration = 3600000000L;

        Profile profile = new Profile();
        profile.setId(1L);
        profile.setFirstname("Brody");
        profile.setLastname("Gaudel");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().id(1L).name("ADMIN").description("ADMIN").build());
        roles.add(Role.builder().id(2L).name("USER").description("USER").build());

        user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setProfile(profile);
        user.setRoles(roles);
    }

    @Test
    void generateToken() {
        String jwt = jwtService.generateToken(user, secret, jwtExpiration);
        assertNotNull(jwt);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        DecodedJWT decodedJWT = JWT.require(algorithm)
                .build()
                .verify(jwt);

        assertEquals("admin", decodedJWT.getSubject());
        assertEquals("Brody Gaudel", decodedJWT.getClaim("fullName").asString());

        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        assertEquals(2, roles.length);
        assertTrue(List.of(roles).contains("USER"));
        assertTrue(List.of(roles).contains("ADMIN"));
    }
}