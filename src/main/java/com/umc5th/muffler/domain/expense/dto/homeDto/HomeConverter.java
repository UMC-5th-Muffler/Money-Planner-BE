package com.umc5th.muffler.domain.expense.dto.homeDto;

import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Level;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HomeConverter {

    public static WholeCalendarResponse toWholeCalendar(
            Goal goal, LocalDate startDate, LocalDate endDate, Long totalCost, List<Long> dailyBudgetList, List<Long> dailyTotalCostList,
            List<Level> dailyRateList, List<Boolean> isZeroDayList, List<CategoryCalendarInfo> categoryCalendarInfo, OtherGoalsResponse otherGoalsInfoList)
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

    public static WholeCalendarResponse otherToWholeCalendar(OtherGoalsResponse otherGoalsInfoList) {
        return WholeCalendarResponse.builder()
                .otherGoalsInfo(otherGoalsInfoList)
                .build();
    }

    public static OtherGoalsResponse toOtherGoals(List<Goal> otherGoals, Integer year, Integer month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        List<OtherGoalsInfo> otherGoalsInfoList = otherGoals.stream()
                .map(otherGoal -> {

                    LocalDate startDate = otherGoal.getStartDate().isBefore(yearMonth.atDay(1))
                            ? yearMonth.atDay(1)
                            : otherGoal.getStartDate();
                    LocalDate endDate = otherGoal.getEndDate().isAfter(yearMonth.atEndOfMonth())
                            ? yearMonth.atEndOfMonth()
                            : otherGoal.getEndDate();

                    List<Level> dailyRate = otherGoal.getDailyPlans().stream()
                            .filter(dailyPlan ->
                                    !dailyPlan.getDate().isBefore(startDate) && !dailyPlan.getDate().isAfter(endDate))
                            .map(DailyPlan::getRate)
                            .map(rate -> (rate != null) ? rate.getTotalLevel() : null)
                            .collect(Collectors.toList());

                    return OtherGoalsInfo.builder()
                            .otherStartDate(startDate)
                            .otherEndDate(endDate)
                            .totalLevelList(dailyRate)
                            .build();
                })
                .collect(Collectors.toList());

        return OtherGoalsResponse.builder()
                .otherGoalsInfoList(otherGoalsInfoList)
                .build();
    }
}
