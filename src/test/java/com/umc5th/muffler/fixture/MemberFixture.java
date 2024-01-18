package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.SocialType;

import java.util.ArrayList;
import java.util.List;

public class MemberFixture {
    public static final Member MEMBER_ONE = Member.builder()
            .id(1L)
            .name("one")
            .email("one@naver.com")
            .socialType(SocialType.KAKAO)
            .build();
    public static final Member MEMBER_TWO = Member.builder()
            .id(1L)
            .name("two")
            .email("two@naver.com")
            .socialType(SocialType.KAKAO)
            .build();

    public static Member create() {
        return Member.builder()
                .id(1L)
                .name("name")
                .email("1")
                .socialType(SocialType.APPLE)
                .goals(new ArrayList<>(List.of(GoalFixture.create())))
                .build();
    }
}
