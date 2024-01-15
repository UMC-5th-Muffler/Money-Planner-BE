package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.SocialType;

public class MemberEntityFixture {
    public static Member create() {
        return Member.builder()
                .id(1L)
                .name("name")
                .email("1")
                .socialType(SocialType.APPLE)
                .build();
    }
}
