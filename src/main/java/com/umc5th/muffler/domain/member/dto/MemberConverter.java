package com.umc5th.muffler.domain.member.dto;

import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.MemberAlarm;

public class MemberConverter {
    public static Member toEntity(Member member, AlarmAgreeUpdateRequest request) {
        MemberAlarm memberAlarm = MemberAlarm.builder()
                .id(member.getMemberAlarm().getId())
                .isDailyPlanRemindAgree(request.getDailyPlanRemindAgree())
                .isGoalEndReportRemindAgree(request.getGoalEndRemindAgree())
                .isTodayEnrollRemindAgree(request.getTodayEnrollRemindAgree())
                .isYesterdayEnrollRemindAgree(request.getYesterdayEnrollRemindAgree())
                .token(member.getMemberAlarm().getToken())
                .build();
        member.setMemberAlarm(memberAlarm);
        return member;
    }
}
