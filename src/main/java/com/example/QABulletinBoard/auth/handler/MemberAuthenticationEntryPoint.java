package com.example.QABulletinBoard.auth.handler;

import com.example.QABulletinBoard.response.ErrorResponse;
import com.example.QABulletinBoard.utils.ErrorResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Exception 발생으로 인해 Security Context에 Authentication이 저장되지 않을 경우 등의 AuthenticationException 발생 시 호출되는 핸들러
@Slf4j
@Component
public class MemberAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Exception exception = (Exception) request.getAttribute("exception");
        ErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);

        logExceptionMessage(authException, exception);
    }

    private void logExceptionMessage(AuthenticationException authException, Exception e) {
        String message = e != null ? e.getMessage() : authException.getMessage();
        log.warn("Unauthorized error happened : {}", message);
    }
}
