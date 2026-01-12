package adapter.in.services;

import application.commands.AuthenticatedUser;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import domain.valueobject.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@ApplicationScoped
public class JwtAdapter {
    @Inject
    @ConfigProperty(name = "privateKey")
    String privateKey;

    public String generateToken(User user) {
        long issuedAt = System.currentTimeMillis();
        long expiration = issuedAt + 60 * 60 * 1000; //1h


        domain.valueobject.UserRole role = switch (user){
            case Member __ -> UserRole.MEMBER;
            case Coach __ -> UserRole.COACH;
            default       -> throw new IllegalArgumentException("unknown user type");
        };

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(user.getId().toString())
                .claim("role",role)
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(expiration))
                .signWith(Keys.hmacShaKeyFor(privateKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    //returns id of requester and role
    public AuthenticatedUser validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(privateKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long id = Long.parseLong(claims.getSubject());

            String roleString = claims.get("role", String.class);

            UserRole role = UserRole.valueOf(roleString);

            return new AuthenticatedUser(id, role);

        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    public Long resolveJWTtoId(String authHeader){
        Long requestedBy=null;
        if(authHeader!=null && authHeader.startsWith("Bearer ")) {
            requestedBy= validateToken(authHeader.substring(7));
        }
        return requestedBy;
    }
}
