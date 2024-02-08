package com.umc5th.muffler.alarm.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class DailyPlanAlarm {
    private final Long memberName;
    private final String alarmToken;
    private final Long dailyBudget;

    @QueryProjection
    public DailyPlanAlarm(Long memberName, Long dailyBudget, String alarmToken) {
        this.memberName = memberName;
        this.dailyBudget = dailyBudget;
        this.alarmToken = alarmToken;
    }
}
