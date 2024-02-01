package com.umc5th.muffler.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoutineAll {
    private Long routineId;
    private String routineTitle;
    private Long routineCost;
    private String categoryIcon;
    private Integer monthlyRepeatDay;
    private RoutineWeeklyDetailDto weeklyDetail;
}
