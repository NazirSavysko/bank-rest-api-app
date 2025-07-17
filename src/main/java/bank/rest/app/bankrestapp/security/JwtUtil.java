package bank.rest.app.bankrestapp.security;

import bank.rest.app.bankrestapp.entity.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.lang.System.currentTimeMillis;

@Component
public class JwtUtil {
    private final String SECRET = "your-256-bit-secret-key-your-256-bit-secret-key";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(final @NotNull Customer customer) {
        return Jwts.builder()
                .setSubject(customer.getAuthUser().getEmail())
                .claim("role", customer.getAuthUser().getCustomerRole().stream().findFirst().orElseThrow().getRoleName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(currentTimeMillis() + 86400000)) // 1 день
                .signWith(key, HS256)
                .compact();
    }

    public Claims extractClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmail(final String token) {
        return extractClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(final String token) {
        try {
            return !extractClaims(token)
                    .getExpiration()
                    .before(new Date());
        } catch (final Exception e) {
            return false;
        }
    }
}
