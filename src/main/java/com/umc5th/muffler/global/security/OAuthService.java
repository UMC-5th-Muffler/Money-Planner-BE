package com.umc5th.muffler.global.security;

import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Role;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.security.jwt.TokenInfo;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // OAuth2 제공자 ID : "kakao"
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 제공자 별 고유 식별자 필드명
        Map<String, Object> attributes = oAuth2User.getAttributes();

        UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);
        Member member = registerUserIfNeed(userProfile);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
                attributes,
                userNameAttributeName
        );
    }

    private Member registerUserIfNeed(UserProfile userProfile) {
        String memberId = userProfile.getId();
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            member = Member.builder()
                    .id(memberId)
                    .socialType(userProfile.getSocialType())
                    .role(Role.USER)
                    .build();
            memberRepository.save(member);
        }
        return member;
    }

    @Transactional
    public boolean determineUserStatusAndSetRefreshToken(Authentication authentication, TokenInfo tokenInfo) {
        String memberId = authentication.getName();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        member.setRefreshToken(tokenInfo.getRefreshToken());

        // 신규 회원인 경우
        if (member.getName() == null) {
            return true;
        }
        return false;
    }
}