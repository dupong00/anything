package com.example.anything.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey secretKey;
    private final long expirationTime;
    private final long reExpirationTime;
    private static final String GRANT_TYPE = "Bearer";

    public JwtProvider(@Value("${jwt.secret}") String secretKey
                       ,@Value("${jwt.expiration-time}") Long expirationTime
                        ,@Value("${jwt.refresh-expiration-time}") Long refreshTime) {

        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET은 최소 32자 이상이어야 합니다.");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
        this.reExpirationTime = refreshTime;
    }

    public JwtToken generateToken(Long userId, String role) {
        long now = (new Date()).getTime();

        Date accessTokenExpire = new Date(now + (expirationTime * 1000));
        Date refreshTokenExpire = new Date(now + (reExpirationTime * 1000));

        String accessToken = Jwts.builder()
                .subject(String.valueOf(userId)) // 주체 설정
                .claim("auth", role) //권한 추가
                .expiration(accessTokenExpire) // 만료 시간 설정
                .signWith(secretKey) //암호화 알고리즘 설정
                .compact(); //직렬화 및 문자열 변환

        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(refreshTokenExpire)
                .signWith(secretKey)
                .compact();

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expirationTime)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get("auth").toString().split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        UserDetails principal = new User(claims.getSubject(),"", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    public Claims parseClaims(String accessToken) {
        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public boolean validateToken(String Token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(Token);
            return true;
        }catch( SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token", e);
        }catch (ExpiredJwtException e){
            log.info("Expired JWT Token", e);
        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token", e);
        }catch (IllegalArgumentException e){
            log.info("JWT claims string is empty", e);
        }catch (Exception e){
            log.error("JWT validation error", e);
        }
        return false;
    }

}
