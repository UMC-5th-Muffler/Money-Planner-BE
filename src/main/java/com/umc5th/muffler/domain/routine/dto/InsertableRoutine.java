package com.umc5th.muffler.domain.routine.dto;

import com.umc5th.muffler.entity.constant.MonthlyRepeatType;
import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
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
    private MonthlyRepeatType routineMonthlyRepeatType;
    private int routineDayOfMonth;

    private Long dailyPlanTotalCost;

    private String memberId;
    private Long categoryId;
    private Long dailyPlanId;

    private boolean isOutDated(LocalDate date) {
        return (!date.isAfter(routineStartDate)) || (routineEndDate != null && date.isAfter(routineEndDate));
    }
    public boolean isValid(LocalDate date) {
        if (isOutDated(date)) {
            return false;
        }
        if (routineType == RoutineType.WEEKLY) {
            LocalDate startDate = this.routineStartDate;
            LocalDate startWeek = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            long weeksSinceStart = ChronoUnit.WEEKS.between(startWeek, date);
            return weeksSinceStart % this.routineWeeklyTerm == 0;
        }
        return true;
    }
}
