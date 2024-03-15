package com.umc5th.muffler.domain.member.dto;

import com.umc5th.muffler.entity.constant.SocialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private SocialType socialType;
    private String idToken;
}
