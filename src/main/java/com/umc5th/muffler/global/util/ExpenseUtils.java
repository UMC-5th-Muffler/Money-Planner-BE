package com.umc5th.muffler.global.util;

import com.umc5th.muffler.entity.Expense;
import java.util.List;

public class ExpenseUtils {
    private ExpenseUtils() {}

    public static Long sumExpenseCosts(List<Expense> expenses) {
        return expenses.stream()
                .mapToLong(Expense::getCost)
                .sum();
    }
}
