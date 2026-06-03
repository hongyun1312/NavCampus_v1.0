package com.hongyun.accounting.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类。
 * 负责生成、解析与校验 JWT Token。
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * 根据认证信息生成 JWT。
     * @param authentication Spring Security 认证对象
     * @return 生成的 JWT 字符串
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJwtTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        // 直接使用密钥的字节，确保一致性
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * 从 JWT 中解析用户名。
     * @param token JWT 字符串
     * @return 用户名
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getJwtIdFromJwtToken(String token) {
        return getClaimsAllowExpired(token).getId();
    }

    public Claims getClaimsAllowExpired(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        }
    }

    /**
     * 校验 JWT 合法性与有效期。
     * @param authToken JWT 字符串
     * @return 是否有效
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("JWT validation failed: " + e.getClass().getName() + " - " + e.getMessage());
        }
        return false;
    }
}
