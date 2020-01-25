package com.dghysc.hy.util;

import com.dghysc.hy.user.model.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Token Until
 * use JWT generate token and get authentication from token
 * @author lorry
 * @author lin864464995@163.com
 * @see io.jsonwebtoken.Claims
 * @see io.jsonwebtoken.Jwts
 * @see org.springframework.security.core.Authentication
 */
@Component
public class TokenUtil {
    private static final long TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    @Value("${manage.secret.token}")
    private String secret;

    /**
     * Generate Token by user
     * @param user the user.
     * @return token string.
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Get User Id From token
     * @param token the token string.
     * @return the user id.
     * @throws ExpiredJwtException if token expired.
     * @throws SignatureException if token invalid.
     */
    public Long getUserIdFromToken(String token)
        throws ExpiredJwtException, SignatureException {
        return new Long(getAllClaimsFromToken(token).getSubject());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}