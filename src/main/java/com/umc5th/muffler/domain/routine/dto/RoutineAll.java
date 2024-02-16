package com.umc5th.muffler.domain.routine.dto;

import lombok.*;

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
