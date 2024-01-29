package com.umc5th.muffler.domain.dailyplan.dto;

import com.mysema.commons.lang.Pair;
import com.umc5th.muffler.domain.category.dto.CategoryConverter;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Rate;
import com.umc5th.muffler.global.util.ExpenseUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeConverter {
    public static WholeCalendar toBasicCalendarResponse(List<InactiveGoalInfo> inactiveGoalsResponse, List<Category> categories) {
        return WholeCalendar.builder()
                .inactiveGoalsResponse(inactiveGoalsResponse)
                .categoryFilters(CategoryConverter.toCategoryDtos(categories))
                .build();
    }

    public static List<GoalDailyInfo> toDailyList(List<DailyPlan> dailyPlans, Map<LocalDate, List<Expense>> expenses) {
        return dailyPlans.stream()
                .map(dailyPlan -> {
                    return createGoalDailyInfo(dailyPlan,
                            expenses.getOrDefault(dailyPlan.getDate(), new ArrayList<>()));
                })
                .collect(Collectors.toList());
    }

    public static ActiveGoalResponse toActiveGoalResponse(Goal goal, LocalDate startDate, LocalDate endDate, Long totalCost, List<GoalDailyInfo> dailyList) {
        return ActiveGoalResponse.builder()
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .goalBudget(goal.getTotalBudget())
                .startDate(startDate)
                .endDate(endDate)
                .totalCost(totalCost)
                .dailyList(dailyList)
                .build();
    }

    public static List<InactiveGoalInfo> toInactiveGoalsResponse(List<Goal> goals, Map<Long, List<Rate>> goalRates,
                                                                 Map<Long, Pair<LocalDate, LocalDate>> goalDates) {
        return goals.stream()
                .map(goal -> {
                    Long goalId = goal.getId();
                    List<Rate> rates = goalRates.get(goalId);
                    Pair<LocalDate, LocalDate> dates = goalDates.get(goalId);
                    return createInactiveGoalInfo(rates, dates.getFirst(), dates.getSecond());
                })
                .collect(Collectors.toList());
    }

    private static GoalDailyInfo createGoalDailyInfo(DailyPlan dailyPlan, List<Expense> expenses) {
        return GoalDailyInfo.builder()
                .dailyBudget(dailyPlan.getBudget())
                .dailyTotalCost(ExpenseUtils.sumExpenseCosts(expenses))
                .dailyRate(dailyPlan.getRate())
                .isZeroDay(dailyPlan.getIsZeroDay())
                .build();
    }

    private static InactiveGoalInfo createInactiveGoalInfo(List<Rate> rates, LocalDate startDate, LocalDate endDate) {
        return new InactiveGoalInfo(startDate, endDate, rates);
    }
}
