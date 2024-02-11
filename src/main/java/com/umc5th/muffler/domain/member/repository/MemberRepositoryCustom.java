package com.umc5th.muffler.domain.member.repository;

import com.umc5th.muffler.domain.member.dto.TodayNotEnrolledMember;
import com.umc5th.muffler.domain.member.dto.YesterdayNotEnrolledMember;
import java.util.List;

public interface MemberRepositoryCustom {
    List<TodayNotEnrolledMember> findTodayNotEnrolledMember();
    List<YesterdayNotEnrolledMember> findYesterdayNotEnrolledMember();
}
