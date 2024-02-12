package com.umc5th.muffler.message.service;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import java.util.List;

public interface AlarmService {
    List<String> sendDailyAlarms(List<DailyPlanAlarm> dailyPlanAlarms);
    List<String> sendTodayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers);
    List<String> sendYesterdayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers);
    List<String> sendEndGoals(List<FinishedGoal> finishedGoals);
}
