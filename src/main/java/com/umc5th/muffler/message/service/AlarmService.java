package com.umc5th.muffler.message.service;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.member.dto.TodayNotEnrolledMember;
import com.umc5th.muffler.domain.member.dto.YesterdayNotEnrolledMember;
import java.util.List;

public interface AlarmService {
    List<String> sendDailyAlarms(List<DailyPlanAlarm> dailyPlanAlarms);
    List<String> sendTodayNotEnrolled(List<TodayNotEnrolledMember> notEnrolledMembers);
    List<String> sendYesterdayNotEnrolled(List<YesterdayNotEnrolledMember> notEnrolledMembers);
}
