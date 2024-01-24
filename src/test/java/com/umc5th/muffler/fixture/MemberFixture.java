package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.SocialType;

public class MemberFixture {
    public static final Member MEMBER_ONE = Member.builder()
            .id("1")
            .name("one")
            .socialType(SocialType.KAKAO)
            .build();
    public static final Member MEMBER_TWO = Member.builder()
            .id("2")
            .name("two")
            .socialType(SocialType.KAKAO)
            .build();
}
