package com.umc5th.muffler.domain.member.dto;

import com.umc5th.muffler.global.security.jwt.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean isNewMember;
    private TokenInfo tokenInfo;
}
