package com.umc5th.muffler.message.service;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import java.util.List;

public interface AlarmService {
    int sendDailyAlarms(List<DailyPlanAlarm> dailyPlanAlarms);
    int sendTodayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers);
    int sendYesterdayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers);
    int sendEndGoals(List<FinishedGoal> finishedGoals);
}
