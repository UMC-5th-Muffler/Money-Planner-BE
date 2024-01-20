package com.umc5th.muffler.global.security.jwt;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.util.ResponseUtils;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String TOKEN_ERROR_MESSAGE = "유효한 인증 토큰이 필요합니다.";
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isLogin(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        try {
            if (token != null && jwtTokenUtils.validateToken(token)) {
                Authentication authentication = jwtTokenUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }
        } catch (CommonException e) {
            log.error("Token Authority Error", e);
        }

        ResponseUtils.sendErrorResponse(response, SC_UNAUTHORIZED, TOKEN_ERROR_MESSAGE);
    }

    private boolean isLogin(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/member/login");
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
