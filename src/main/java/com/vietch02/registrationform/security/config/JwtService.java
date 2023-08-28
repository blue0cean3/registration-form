package com.vietch02.registrationform.security.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "83DF17247DD69B95B86447D23CE4C";

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    // extract single claim
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimResult) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimResult.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaim,
            UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaim)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUsername(jwtToken);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExperation(jwtToken).before(new Date());
    }

    private Date extractExperation(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInkey()) // when try to create or recall a token we need signing key
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        // signing key is the secret used to digitally sign jwt
        // use to create signature part of jwt
        // use to verify the center of jwt is who it claim to be
        // and ensure that message wasnt change
        // minimun required for jwt is 256-bit
    }
    /*
     * JSON web token contains 3 part: HEADER: { "alg": "HS256", "typ": "JWT" }
     * "alg" : alogorithm PAYLOAD: { "sub": "1234567890", "name": "John Doe", "iat":
     * 1516239022 } contains the claims : the statement about entity (users data,
     * information) VERIFY SIGNATURE: HMACSHA256( base64UrlEncode(header) + "." +
     * base64UrlEncode(payload),
     *
     * your-256-bit-secret
     *
     * )
     *
     */

    private SecretKey getSignInkey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
