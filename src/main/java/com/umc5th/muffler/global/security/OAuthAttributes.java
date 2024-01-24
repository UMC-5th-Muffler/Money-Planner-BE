package com.umc5th.muffler.global.security;

import com.umc5th.muffler.entity.constant.SocialType;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CommonException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    KAKAO("kakao", (attributes) -> {
        return new UserProfile(
                String.valueOf(attributes.get("id")),
                SocialType.KAKAO
        );
    });

    private final String registrationId;
    private final Function<Map<String, Object>, UserProfile> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, UserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_PERMISSION, "지원하지 않는 소셜 로그인입니다."))
                .of.apply(attributes);
    }
}
