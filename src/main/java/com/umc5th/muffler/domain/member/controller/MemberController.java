package com.umc5th.muffler.domain.member.controller;

import com.umc5th.muffler.domain.member.dto.RefreshTokenRequest;
import com.umc5th.muffler.domain.member.service.MemberService;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login/kakao")
    public RedirectView kakaoLogin() {
        return new RedirectView("/oauth2/authorization/kakao");
    }

    @GetMapping("/login/apple")
    public RedirectView appleLogin() {
        return new RedirectView("/oauth2/authorization/apple");
    }

    @PostMapping("/refresh-token")
    public Response<TokenInfo> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        return Response.success(memberService.refreshAccessToken(request.getRefreshToken()));
    }

}
