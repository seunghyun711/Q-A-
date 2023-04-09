package com.example.QABulletinBoard.auth.config;

import com.example.QABulletinBoard.auth.filter.JwtAuthenticationFilter;
import com.example.QABulletinBoard.auth.filter.JwtVerificationFilter;
import com.example.QABulletinBoard.auth.handler.MemberAccessDeniedHandler;
import com.example.QABulletinBoard.auth.handler.MemberAuthenticationEntryPoint;
import com.example.QABulletinBoard.auth.handler.MemberAuthenticationFailureHandler;
import com.example.QABulletinBoard.auth.handler.MemberAuthenticationSuccessHandler;
import com.example.QABulletinBoard.auth.jwt.JwtTokenizer;
import com.example.QABulletinBoard.auth.utils.CustomAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfiguration implements WebMvcConfigurer {

    private final CustomAuthorityUtils authorityUtils;

    public SecurityConfiguration(CustomAuthorityUtils authorityUtils) {
        this.authorityUtils = authorityUtils;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin() // 동일출처로 들어오는 reqeust만 페이지 렌더링 허용
                .and()
                .csrf().disable() // CSRF 공격에 대한 스프링 시큐리티에 대한 설정 비활성화
                .cors(Customizer.withDefaults())
                // 세션을 설정하지 않도록 설정
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomFilterConfigurer()) // Custom된 Configuration 추가
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers(HttpMethod.POST, "/*/members").permitAll()
                        .antMatchers(HttpMethod.POST, "/*/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/*/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET, "/*/members").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/*/members/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/*/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.POST, "/*/coffees").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/*/coffees/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/*/coffees/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/*/coffees").permitAll()
                        .antMatchers(HttpMethod.DELETE, "/*/coffees").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/*/orders").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/*/orders").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/*/orders/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/*/orders").hasRole("USER")
                        .mvcMatchers(HttpMethod.POST, "/*/questions/*/qna-answers").hasRole("ADMIN")
                        .mvcMatchers(HttpMethod.PATCH, "/*/questions/**").hasRole("USER")
                        .mvcMatchers(HttpMethod.GET, "/*/questions/**").hasRole("USER")
                        .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // CORS 정책 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // 모든 출처에 대해 스크립트 기반 HTTP 통신 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));// 해당 HTTP 메서드에 대한 HTTP 통신 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // CorsConfigurationSource의 구현 클래스 객체 생성
        source.registerCorsConfiguration("/**",configuration); // 모든 URL에 위 CORS 정책 적용
        return source;
    }

    // 이전에 구현한 JwtAuthenticationFilter를 등록하는 메서드
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception{
            // AuthenticationManager 객체를 얻는다.
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter =  new JwtAuthenticationFilter(authenticationManager, jwtTokenizer());
            jwtAuthenticationFilter.setFilterProcessesUrl("/QA/auth/login");
            // AuthenticationSuccess/FailureHandler 추가
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            // JwtVerificationFilter 추가
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer(), authorityUtils);

            builder.addFilter(jwtAuthenticationFilter)// JwtAuthenticationFilter를 스프링 시큐리티 필터에 추가
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class); // JwtAuthenticationFilter 뒤에 JwtVerificationFilter 추가
        }
    }

    @Bean
    public JwtTokenizer jwtTokenizer(){
        return new JwtTokenizer();
    }

}
