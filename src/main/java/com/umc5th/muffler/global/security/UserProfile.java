package com.umc5th.muffler.global.security;

import com.umc5th.muffler.entity.constant.SocialType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserProfile {
    private final String id;
    private final SocialType socialType;
}
