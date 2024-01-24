package com.umc5th.muffler.domain.expense.dto.homeDto;

import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Level;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HomeConverter {

    public static WholeCalendarResponse toWholeCalendar(
            Goal goal, LocalDate startDate, LocalDate endDate, Long totalCost, List<Long> dailyBudgetList, List<Long> dailyTotalCostList,
            List<Level> dailyRateList, List<Boolean> isZeroDayList, List<CategoryCalendarInfo> categoryCalendarInfo, List<OtherGoalsInfo> otherGoalsInfoList)
    {
        List<WholeCalendarDailyInfo> dailyInfoList = IntStream.range(0, dailyBudgetList.size())
                .mapToObj(i -> WholeCalendarDailyInfo.builder()
                        .dailyBudget(dailyBudgetList.get(i))
                        .dailyTotalCost(dailyTotalCostList.get(i))
                        .dailyRate(dailyRateList.get(i))
                        .isZeroDay(isZeroDayList.get(i))
                        .build())
                .collect(Collectors.toList());

        return WholeCalendarResponse.builder()
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .goalBudget(goal.getTotalBudget())
                .goalStartDate(startDate)
                .goalEndDate(endDate)
                .totalCost(totalCost)
                .categoryCalendarInfo(categoryCalendarInfo)
                .dailyList(dailyInfoList)
                .otherGoalsInfo(otherGoalsInfoList)
                .build();
    }

    public static WholeCalendarResponse toOtherGoalsCalendar(List<OtherGoalsInfo> otherGoalsInfoList) {

        return WholeCalendarResponse.builder()
                .otherGoalsInfo(otherGoalsInfoList)
                .build();
    }
}
