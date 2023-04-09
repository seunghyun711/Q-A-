package com.example.QABulletinBoard.auth.filter;

import com.example.QABulletinBoard.auth.jwt.JwtTokenizer;
import com.example.QABulletinBoard.auth.utils.CustomAuthorityUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
클라이언트 측에서 전송된 request header에 포함된 JWT에 대해 검증 작업을 수행하는 클래스
OncePerRequestFilter를 확장하여 request 당 한 번만 실행되는 Security Filter를 구현
 */
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> claims = verifyJws(request);// JWT 검증할 때 사용하는 메서드
        setAuthenticationToContext(claims); // Authentication 객체를 SecurityContext에 저장하기 위한 메서드

        filterChain.doFilter(request, response); // 다음 필터 호출
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer", ""); // request의 header에서 JWT 얻는다.
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()); // JWT 서명 검증위한 Secret Key
        Map<String, Object> claims = jwtTokenizer.getClaims(jws,base64EncodedSecretKey).getBody(); // JWT에서 Claims 파싱

        return claims;
    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("username"); // JWT에서 파싱한 Claims에서 username을 얻는다.
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List) claims.get("roles")); // JWT의 Claims에서 얻은 권한 정보를 기반으로 List<GrantedAuthority> 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities); // Authentication 객체 생성
        SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 Authentication 객체 저장
    }

    // 특정 조건에 부합하면 해당 Filter의 동작을 수행하지 않고 다음 Filter로 넘어가게 한다.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization"); // Authorization Header의 값을 덛는디.

        // 값이 null 이거나 Authorization 값이 "Bearer"로 시작하지 않음녀 해당 Filter의 동작을 수행하지 않는다.

        return authorization == null || !authorization.startsWith("Bearer");
    }
}
