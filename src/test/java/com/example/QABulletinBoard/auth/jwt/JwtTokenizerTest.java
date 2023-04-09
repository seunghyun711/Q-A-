package com.example.QABulletinBoard.auth.jwt;

import io.jsonwebtoken.io.Decoders;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtTokenizerTest {
    private static JwtTokenizer jwtTokenizer;
    private String secretKey;
    private String base64EncodedSecretKey;

    // SecretKey를 base64 형식으로 인코딩하고 인코딩 된 SecretKey를 각 테스트 케이스에서 사용
    @BeforeAll
    public void init(){
        jwtTokenizer = new JwtTokenizer();
        secretKey = "hong12391820931820931249123534212";

        base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(secretKey);
    }

    // plain text인 SecretKey가 base64 형식으로 정상적으로 인코딩 되는지 테스트
    @Test
    public void encodeBase64SecretKeyTest(){
        System.out.println(base64EncodedSecretKey);

        MatcherAssert.assertThat(secretKey, Matchers.is(new String(Decoders.BASE64URL.decode(base64EncodedSecretKey))));
    }

    // AccessToken() 생성 테스트
    @Test
    public void generateAccessTokenTest() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", 1);
        claims.put("roles", List.of("USER"));

        String subject = "test access token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        Date expiration = calendar.getTime();

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);
        System.out.println(accessToken);

        MatcherAssert.assertThat(accessToken, Matchers.notNullValue());
    }

    // Refresh Token 생성 테스트
    @Test
    public void generateRefreshTokenTest(){
        String subject = "test refresh token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        Date expiration = calendar.getTime();

        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        System.out.println(refreshToken);

        MatcherAssert.assertThat(refreshToken, Matchers.notNullValue());
    }

}