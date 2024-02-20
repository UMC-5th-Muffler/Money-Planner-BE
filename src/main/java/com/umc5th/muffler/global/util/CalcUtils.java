package com.umc5th.muffler.global.util;

import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import java.util.List;

public class CalcUtils {
    private CalcUtils() {}

    public static Long sumExpenseCosts(List<Expense> expenses) {
        return expenses.stream()
                .mapToLong(Expense::getCost)
                .sum();
    }

    public static Long sumDailyPlanTotalCost(List<DailyPlan> dailyPlans) {
        return dailyPlans.stream()
                .mapToLong(DailyPlan::getTotalCost)
                .sum();
    }
}
