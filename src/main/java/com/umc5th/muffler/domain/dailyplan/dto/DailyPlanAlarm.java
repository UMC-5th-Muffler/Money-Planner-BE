package com.umc5th.muffler.domain.dailyplan.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.umc5th.muffler.message.dto.Alarmable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyPlanAlarm implements Alarmable {
    private static final String TITLE = "당일 목표 금액 리마인드";
    private Long memberName;
    private String alarmToken;
    private Long dailyBudget;

    @QueryProjection
    public DailyPlanAlarm(Long memberName, Long dailyBudget, String alarmToken) {
        this.memberName = memberName;
        this.dailyBudget = dailyBudget;
        this.alarmToken = alarmToken;
    }

    @Override
    public String getToken() {
        return alarmToken;
    }
}
