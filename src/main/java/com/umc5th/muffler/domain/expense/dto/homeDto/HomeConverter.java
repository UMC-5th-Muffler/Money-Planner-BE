package com.umc5th.muffler.domain.expense.dto.homeDto;

import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Level;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HomeConverter {

    public static WholeCalendarResponse toWholeCalendar(
            LocalDate date, Goal goal, LocalDate startDate, LocalDate endDate, Long totalCost,
            List<Long> dailyBudgetList, List<Long> dailyTotalCostList, List<Boolean> isZeroDayList, List<CategoryCalendarInfo> categoryCalendarInfo)
    {
        List<WholeCalendarDailyInfo> dailyInfoList = IntStream.range(0, dailyBudgetList.size())
                .mapToObj(i -> WholeCalendarDailyInfo.builder()
                        .dailyBudget(dailyBudgetList.get(i))
                        .dailyTotalCost(dailyTotalCostList.get(i))
                        .dailyRate(Level.HIGH) // 임시
                        .isZeroDay(isZeroDayList.get(i))
                        .build())
                .collect(Collectors.toList());

        return WholeCalendarResponse.builder()
                .calendarDate(date)
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .goalBudget(goal.getTotalBudget())
                .goalStartDate(startDate)
                .goalEndDate(endDate)
                .totalCost(totalCost)
                .categoryCalendarInfo(categoryCalendarInfo)
                .dailyList(dailyInfoList)
                .build();
    }
}
