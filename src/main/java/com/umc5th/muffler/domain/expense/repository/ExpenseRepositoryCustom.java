package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ExpenseRepositoryCustom {
    Map<LocalDate, List<Expense>> findByMemberAndDateRangeGroupedByDate(String memberId, LocalDate startDate, LocalDate endDate);
    Map<LocalDate, List<Expense>> findByMemberAndCategoryAndDateRangeGroupedByDate(String memberId, Long categoryId, LocalDate startDate, LocalDate endDate);
    Slice<Expense> findAllByMemberAndDateAndCategoryId(String memberId, LocalDate startDate, LocalDate endDate, Long categoryId, Pageable pageable);
    Slice<Expense> findAllByMemberAndDate(String memberId, LocalDate date, LocalDateTime lastCreatedAt, Pageable pageable);
}
