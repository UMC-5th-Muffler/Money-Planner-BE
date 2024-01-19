package com.umc5th.muffler.domain.routine.dto;

import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class InsertableRoutine {
    private Long routineId;
    private RoutineType routineType;
    private String routineTitle;
    private String routineMemo;
    private Long routineCost;
    private LocalDate routineStartDate;
    private LocalDate routineEndDate;
    private int routineWeeklyTerm;
    private DayOfWeek routineDayOfWeek;
    private int routineDayOfMonth;

    private Long memberId;
    private Long categoryId;
}
