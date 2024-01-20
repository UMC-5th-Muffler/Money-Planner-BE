package com.umc5th.muffler.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc5th.muffler.global.security.jwt.JwtTokenUtils;
import com.umc5th.muffler.global.security.jwt.TokenInfo;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtils jwtTokenUtils;
    private final OAuthService oAuthService;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        TokenInfo tokenInfo = jwtTokenUtils.generateToken(authentication);

        boolean isNewUser = oAuthService.determineUserStatusAndSetRefreshToken(authentication, tokenInfo);
        if (isNewUser) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenInfo));
        response.getWriter().flush();
    }
}
