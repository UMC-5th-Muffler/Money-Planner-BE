package com.umc5th.muffler.domain.expense.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.QExpense;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<LocalDate, List<Expense>> findByMemberAndDateRangeGroupedByDate(String memberId, LocalDate startDate, LocalDate endDate) {
        QExpense expense = QExpense.expense;

        List<Expense> expenses = queryFactory
                .selectFrom(expense)
                .where(expense.member.id.eq(memberId)
                        .and(expense.date.between(startDate, endDate))
                ).orderBy(expense.date.asc())
                .fetch();

        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getDate));
    }

    @Override
    public Map<LocalDate, List<Expense>> findByMemberAndCategoryAndDateRangeGroupedByDate(String memberId, Long categoryId,
                                                                                          LocalDate startDate, LocalDate endDate) {
        QExpense expense = QExpense.expense;

        List<Expense> expenses = queryFactory
                .selectFrom(expense)
                .where(expense.member.id.eq(memberId)
                        .and(expense.category.id.eq(categoryId))
                        .and(expense.date.between(startDate, endDate))
                ).orderBy(expense.date.asc())
                .fetch();

        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getDate));
    }

    @Override
    public Long sumCategoryExpenseWithinGoal(String memberId, Category category, Goal goal) {
        QExpense expense = QExpense.expense;

        Long sum = queryFactory
                .select(expense.cost.sum())
                .from(expense)
                .where(expense.member.id.eq(memberId)
                        .and(expense.category.id.eq(category.getId()))
                        .and(expense.date.between(goal.getStartDate(), goal.getEndDate()))
                ).fetchOne();
        return sum == null ? 0L : sum;
    }

    @Override
    public Long sumCostByMemberAndDateBetween(String memberId, LocalDate startDate, LocalDate endDate) {
        QExpense expense = QExpense.expense;

        Long sum = queryFactory
                .select(expense.cost.sum())
                .from(expense)
                .where(expense.member.id.eq(memberId)
                        .and(expense.date.between(startDate, endDate))
                ).fetchOne();
        return sum == null ? 0L : sum;
    }


}
