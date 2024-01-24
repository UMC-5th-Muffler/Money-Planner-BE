package com.umc5th.muffler.domain.member.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_TOKEN;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;

import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.security.jwt.JwtTokenUtils;
import com.umc5th.muffler.global.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public TokenInfo refreshAccessToken(String refreshToken) {
        if (!jwtTokenUtils.validateToken(refreshToken)) {
            throw new CommonException(INVALID_TOKEN);
        }
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
        TokenInfo newToken = jwtTokenUtils.generateToken(authentication);

        member.setRefreshToken(newToken.getRefreshToken());
        return newToken;
    }
}
