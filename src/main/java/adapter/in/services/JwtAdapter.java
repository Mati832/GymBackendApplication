package adapter.in.services;

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

    public String generateToken(String id) {
        long issuedAt = System.currentTimeMillis();
        long expiration = issuedAt + 60 * 60 * 1000; //1h


        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(id)
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(expiration))
                .signWith(Keys.hmacShaKeyFor(privateKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
    //returns id of requester
    public Long validateToken(String token) {
        try {
            return Long.parseLong(Jwts.parserBuilder()
                    .setSigningKey(privateKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (JwtException e) {
            return null;
        }
    }
}
