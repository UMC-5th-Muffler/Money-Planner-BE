package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.SocialType;
import java.util.ArrayList;
import java.util.List;

public class MemberEntityFixture {
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
