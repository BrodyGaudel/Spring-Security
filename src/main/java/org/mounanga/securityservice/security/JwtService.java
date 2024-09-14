package org.mounanga.securityservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.jetbrains.annotations.NotNull;
import org.mounanga.securityservice.entity.Profile;
import org.mounanga.securityservice.entity.Role;
import org.mounanga.securityservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    public String generateToken(@NotNull User user, String secret, Long jwtExpiration) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Date expirationDate = new Date(System.currentTimeMillis() + jwtExpiration);
        List<String> roles = getListOfNamesOfRoles(user.getRoles());
        return JWT.create()
                .withSubject(user.getUsername())
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .withClaim("fullName", getFullName(user.getProfile()))
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }


    private String getFullName(@NotNull final Profile profile) {
        if(profile.getFullName() == null) {
            return "UNKNOWN";
        }
        return profile.getFullName();
    }

    private List<String> getListOfNamesOfRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }
        return roles.stream().map(Role::getName).toList();
    }



}
