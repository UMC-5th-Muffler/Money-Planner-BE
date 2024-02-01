package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Expense;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseRepositoryCustom {
    Map<LocalDate, List<Expense>> findByMemberAndDateRangeGroupedByDate(String memberId, LocalDate startDate, LocalDate endDate);
    Map<LocalDate, List<Expense>> findByMemberAndCategoryAndDateRangeGroupedByDate(String memberId, Long categoryId, LocalDate startDate, LocalDate endDate);
}
