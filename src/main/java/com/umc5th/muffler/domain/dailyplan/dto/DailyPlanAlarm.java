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
    private String memberName;
    private Long dailyBudget;
    private String alarmToken;

    @Override
    public String getToken() {
        return alarmToken;
    }
}
