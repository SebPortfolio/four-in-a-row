package de.paulm.four_in_a_row.service;

import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.security.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Generische Methode, um beliebige Daten (Claims) zu lesen
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        if (userDetails instanceof User user) {
            extraClaims.put("id", user.getId());
            List<String> roles = user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            extraClaims.put("roles", roles);
        }
        return buildAccessToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildAccessToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token, UserDetails userDetails) {
        final Claims claims = extractAllClaims(token);
        final String username = claims.getSubject();

        boolean isUsernameCorrect = username.equals(userDetails.getUsername());
        boolean isNotExpired = claims.getExpiration().before(new Date()) == false;
        boolean isIssuedAfterPasswordChange = isIssuedAfterPasswordChange(claims, userDetails);
        return (isUsernameCorrect && isNotExpired && isIssuedAfterPasswordChange);
    }

    private boolean isIssuedAfterPasswordChange(Claims claims, UserDetails userDetails) {
        final Date issuedAt = claims.getIssuedAt();

        if (userDetails instanceof User user) {
            if (user.getLastPasswordChangeAt() != null) {
                if (issuedAt == null)
                    return false;

                Date lastChange = Timestamp.valueOf(user.getLastPasswordChangeAt());
                return issuedAt.getTime() >= lastChange.getTime();
            }
        }
        return true;
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}