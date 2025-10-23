package com.sdu.open.source.site.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类，用于生成和验证JWT令牌
 */
@Component
public class JwtTokenUtil {

    // 默认过期时间24小时
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;
    // 密码重置令牌过期时间1小时
    private static final long RESET_TOKEN_VALIDITY = 60 * 60 * 1000;

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取过期日期
     *
     * @param token 令牌
     * @return 过期日期
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取声明
     *
     * @param token          令牌
     * @param claimsResolver 声明解析器
     * @return 声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     *
     * @param token 令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * 检查令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 为用户生成令牌
     *
     * @param userDetails 用户详情
     * @return 令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername(), JWT_TOKEN_VALIDITY);
    }

    /**
     * 生成密码重置令牌（1小时过期，带自定义声明）
     *
     * @param username 用户名
     * @return 重置令牌
     */
    public String generateResetToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("reset_password", true); // 标记为密码重置令牌
        return doGenerateToken(claims, username, RESET_TOKEN_VALIDITY);
    }

    /**
     * 生成令牌
     *
     * @param claims  声明
     * @param subject 主题
     * @return 令牌
     */
    private String doGenerateToken(Map<String, Object> claims, String subject, long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 验证是否为密码重置令牌
     *
     * @param token 令牌
     * @return 是否为重置令牌
     */
    public Boolean isResetToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return Boolean.TRUE.equals(claims.get("reset_password", Boolean.class));
        } catch (Exception e) {
            return false;
        }
    }
}