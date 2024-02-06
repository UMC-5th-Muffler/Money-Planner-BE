package com.umc5th.muffler.domain.dailyplan.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WholeCalendar {
    private GoalInfo goalInfo;
    private List<DailyInfo> dailyList;
}
