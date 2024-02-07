package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseRepositoryCustom {
    Map<LocalDate, List<Expense>> findByMemberAndDateRangeGroupedByDate(String memberId, LocalDate startDate, LocalDate endDate);
    Map<LocalDate, List<Expense>> findByMemberAndCategoryAndDateRangeGroupedByDate(String memberId, Long categoryId, LocalDate startDate, LocalDate endDate);
    Long sumCategoryExpenseWithinGoal(String memberId, Category category, Goal goal);
    Long sumCostByMemberAndDateBetween(String memberId, LocalDate startDate, LocalDate endDate);
}
