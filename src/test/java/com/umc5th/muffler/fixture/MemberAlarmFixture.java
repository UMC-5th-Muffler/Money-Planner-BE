package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.MemberAlarm;

public class MemberAlarmFixture {
    public static final MemberAlarm ALL_AGREE = MemberAlarm.builder()
            .isDailyPlanRemindAgree(true)
            .isYesterdayEnrollRemindAgree(true)
            .isTodayEnrollRemindAgree(true)
            .isGoalEndReportRemindAgree(true)
            .token("token")
            .build();
    public static final MemberAlarm ALL_EXCEPT_DAILY_PLAN = MemberAlarm.builder()
            .isDailyPlanRemindAgree(false)
            .isYesterdayEnrollRemindAgree(true)
            .isTodayEnrollRemindAgree(true)
            .isGoalEndReportRemindAgree(true)
            .token("token")
            .build();
    public static final MemberAlarm ALL_EXCEPT_YESTERDAY = MemberAlarm.builder()
            .isDailyPlanRemindAgree(true)
            .isYesterdayEnrollRemindAgree(false)
            .isTodayEnrollRemindAgree(true)
            .isGoalEndReportRemindAgree(true)
            .token("token")
            .build();
    public static final MemberAlarm ALL_EXCEPT_TODAY = MemberAlarm.builder()
            .isDailyPlanRemindAgree(true)
            .isYesterdayEnrollRemindAgree(true)
            .isTodayEnrollRemindAgree(false)
            .isGoalEndReportRemindAgree(true)
            .token("token")
            .build();
    public static final MemberAlarm ALL_EXCEPT_GOAL_END = MemberAlarm.builder()
            .isDailyPlanRemindAgree(true)
            .isYesterdayEnrollRemindAgree(true)
            .isTodayEnrollRemindAgree(true)
            .isGoalEndReportRemindAgree(false)
            .token("token")
            .build();
    public static final MemberAlarm ALL_EXCEPT_TOKEN = MemberAlarm.builder()
            .isDailyPlanRemindAgree(true)
            .isYesterdayEnrollRemindAgree(true)
            .isTodayEnrollRemindAgree(true)
            .isGoalEndReportRemindAgree(true)
            .build();
}
