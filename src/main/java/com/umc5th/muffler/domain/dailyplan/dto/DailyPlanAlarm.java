package com.umc5th.muffler.domain.dailyplan.dto;

import com.umc5th.muffler.message.dto.Alarmable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyPlanAlarm implements Alarmable {
    private String memberName;
    private Long dailyBudget;
    private String alarmToken;

    @Override
    public String getToken() {
        return alarmToken;
    }
}
