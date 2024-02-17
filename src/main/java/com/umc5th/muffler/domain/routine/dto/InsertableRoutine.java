package com.umc5th.muffler.domain.routine.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
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

    private Long dailyPlanTotalCost;

    private String memberId;
    private Long categoryId;
    private Long dailyPlanId;

    @QueryProjection
    public InsertableRoutine(
        Long routineId,
        RoutineType routineType,
        String routineTitle,
        String routineMemo,
        Long routineCost,
        LocalDate routineStartDate,
        LocalDate routineEndDate,
        int routineWeeklyTerm,
        DayOfWeek routineDayOfWeek,
        int routineDayOfMonth,
        Long dailyPlanTotalCost,
        String memberId,
        Long categoryId,
        Long dailyPlanId
    ) {
        this.routineId = routineId;
        this.routineType = routineType;
        this.routineTitle = routineTitle;
        this.routineMemo = routineMemo;
        this.routineCost = routineCost;
        this.routineStartDate = routineStartDate;
        this.routineEndDate = routineEndDate;
        this.routineWeeklyTerm = routineWeeklyTerm;
        this.routineDayOfWeek = routineDayOfWeek;
        this.routineDayOfMonth = routineDayOfMonth;
        this.dailyPlanTotalCost = dailyPlanTotalCost;
        this.memberId = memberId;
        this.categoryId = categoryId;
        this.dailyPlanId = dailyPlanId;
    }

    private boolean isOutDated(LocalDate date) {
        return (date.isBefore(routineStartDate)) || (routineEndDate != null && date.isAfter(routineEndDate));
    }
    public boolean isValid(LocalDate date) {
        if (isOutDated(date)) {
            return false;
        }
        if (routineType == RoutineType.WEEKLY) {
            long between = ChronoUnit.WEEKS.between(routineStartDate, date);
            return between % routineWeeklyTerm == 0;
        }
        return true;
    }
}
