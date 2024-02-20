package com.umc5th.muffler.domain.dailyplan.dto;

import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Rate;
import com.umc5th.muffler.global.util.CalcUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeConverter {
    public static WholeCalendar toBasicCalendar(List<DailyInfo> inactiveDailies) {
        return WholeCalendar.builder()
                .dailyList(inactiveDailies)
                .build();
    }

    public static WholeCalendar toCategoryCalendar(CategoryInfo categoryInfo, List<DailyInfo> categoryDailies, List<DailyInfo> inactiveDailies) {
        List<DailyInfo> dailyList = new ArrayList<>(categoryDailies);
        dailyList.addAll(inactiveDailies);
        dailyList.sort(Comparator.comparing(DailyInfo::getDate));

        return WholeCalendar.builder()
                .calendarInfo(categoryInfo)
                .dailyList(dailyList)
                .build();
    }

    public static WholeCalendar toGoalCalendar(GoalInfo goalInfo, List<DailyInfo> activeDailies, List<DailyInfo> inactiveDailies) {
        List<DailyInfo> dailyList = new ArrayList<>(activeDailies);
        dailyList.addAll(inactiveDailies);
        dailyList.sort(Comparator.comparing(DailyInfo::getDate));

        return WholeCalendar.builder()
                .calendarInfo(goalInfo)
                .dailyList(dailyList)
                .build();
    }

    public static GoalInfo toGoalInfo(Goal goal, Long totalCost) {
        return GoalInfo.builder()
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .goalBudget(goal.getTotalBudget())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .totalCost(totalCost)
                .build();
    }

    public static List<DailyInfo> toActiveDailies(List<DailyPlan> dailyPlans) {
        return dailyPlans.stream()
                .map(HomeConverter::createActiveDaily)
                .collect(Collectors.toList());
    }

    private static DailyInfo createActiveDaily(DailyPlan dailyPlan) {
        return (DailyInfo) ActiveDaily.builder()
                .date(dailyPlan.getDate())
                .dailyBudget(dailyPlan.getBudget())
                .dailyTotalCost(dailyPlan.getTotalCost())
                .dailyRate(dailyPlan.getRate())
                .isZeroDay(dailyPlan.getIsZeroDay())
                .build();
    }

    public static List<DailyInfo> toCategoryDaily(Map<LocalDate, List<Expense>> expenses, Map<LocalDate, Rate> rates, LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    return (DailyInfo) new CategoryDaily(
                            date,
                            CalcUtils.sumExpenseCosts(expenses.getOrDefault(date, Collections.emptyList())),
                            rates.get(date)
                    );
                }).collect(Collectors.toList());
    }
}
