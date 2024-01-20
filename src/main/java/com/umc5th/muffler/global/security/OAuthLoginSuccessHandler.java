package com.umc5th.muffler.global.security;

import com.umc5th.muffler.global.security.jwt.JwtTokenUtils;
import com.umc5th.muffler.global.security.jwt.TokenInfo;
import com.umc5th.muffler.global.util.ResponseUtils;
import java.io.IOException;
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

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        TokenInfo tokenInfo = jwtTokenUtils.generateToken(authentication);
        int statusCode = HttpServletResponse.SC_OK;

        boolean isNewUser = oAuthService.determineUserStatusAndSetRefreshToken(authentication, tokenInfo);
        if (isNewUser) {
            statusCode = HttpServletResponse.SC_CREATED;
        }
        ResponseUtils.sendSuccessResponse(response, statusCode, tokenInfo);
    }
}
