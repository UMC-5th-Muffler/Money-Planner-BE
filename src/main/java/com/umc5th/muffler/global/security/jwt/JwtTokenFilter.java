package com.umc5th.muffler.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.response.exception.CommonException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isLogin(request)) {
            chain.doFilter(request, response);
            return;
        }

        String token = getToken((HttpServletRequest) request);
        try {
            if (token != null && jwtTokenUtils.validateToken(token)) {
                Authentication authentication = jwtTokenUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
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
