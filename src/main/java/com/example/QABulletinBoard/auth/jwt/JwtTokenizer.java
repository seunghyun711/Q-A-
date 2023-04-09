package com.example.QABulletinBoard.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class JwtTokenizer {
    // JWT 생성/검증 시 사용되는 SecretKey 정보
    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    // Access Token 만료 시간 정보
    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    // Refresh Token 만료 시간 정보
    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    // Plain text의 SecretKey를 Base64 형식의 문자열로 인코딩
    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 인증된 사용자에게 JWT를 발급해주는 JWT 생성 메서드
    public String generateAccessToken(Map<String, Object> claims,
                                      String subject,
                                      Date expiration,
                                      String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey); // Base64 형식 Secret Key 문자열로 Key 객체를 얻는다.

        return Jwts.builder()
                .setClaims(claims) // JWT에 포함될 Custom Claims 추가 -> 여기에는 인증된 사용자와 관련된 정보가 추가됨
                .setSubject(subject) // JWT 에 대한 제목
                .setIssuedAt(Calendar.getInstance().getTime()) // JWT의 발행일자 설정
                .setExpiration(expiration) // JWT의 만료일시 설정
                .signWith(key) // 서명을 위한 Key 객체 설정
                .compact(); // JWT 생성 & 직렬화
    }

    // AccessToken 만료시 새로 생성해주는 RefreshToken을 생성하는 메서드
    public String generateRefreshToken(String subject,
                                       Date expiration,
                                       String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    // JWT의 서명에 사용할 Secret Key 생성
    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] bytes = Decoders.BASE64.decode(base64EncodedSecretKey); // 인코딩한 Secret Key를 디코딩 후, byte array로 반환
        Key key = Keys.hmacShaKeyFor(bytes); // byte array를 기반으로 HMAC 알고리즘 적용한 Key 객체 생성

        return key;
    }

    // JWT 검증 메서드(단순 검증)
    public void verifySignature(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Jwts.parserBuilder()
                .setSigningKey(key) // 서명에 상요된 Secret Key 생성
                .build()
                .parseClaimsJws(jws); // JWT 파싱하여 Claims를 얻는다.
    }
    // 검증 후 Claims 반환
    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
        return claims;
    }

    public Date getTokenExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);
        Date expiration = calendar.getTime();

        return expiration;
    }




}
