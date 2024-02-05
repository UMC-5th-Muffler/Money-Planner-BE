package com.umc5th.muffler.domain.expense.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.QCategory;
import com.umc5th.muffler.entity.QExpense;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    public Slice<Expense> findAllByMemberAndDate(String memberId, LocalDate date, Long lastExpenseId, Pageable pageable) {
        QExpense expense = QExpense.expense;
        QCategory category = QCategory.category;

        JPAQuery<Expense> query = queryFactory
                .selectFrom(expense)
                .leftJoin(expense.category, category).fetchJoin()
                .where(expense.member.id.eq(memberId),
                        expense.date.eq(date),
                        lastExpenseId == null ? null : expense.id.lt(lastExpenseId))
                .orderBy(expense.id.desc())
                .limit(pageable.getPageSize() + 1);

        List<Expense> expenses = query.fetch();

        boolean hasNext = false;
        if (expenses.size() > pageable.getPageSize()) {
            expenses.remove(expenses.size() - 1);
            hasNext = true;
        }

        return new SliceImpl<>(expenses, pageable, hasNext);
    }

    @Override
    public Slice<Expense> findAllByMemberAndDateAndCategoryId(String memberId, LocalDate startDate, LocalDate endDate, Long categoryId, Pageable pageable) {
        QExpense expense = QExpense.expense;
        QCategory category = QCategory.category;

        JPAQuery<Expense> query = queryFactory
                .selectFrom(expense)
                .leftJoin(expense.category, category).fetchJoin()
                .where(expense.member.id.eq(memberId)
                        .and(expense.date.between(startDate, endDate))
                        .and(categoryId != null ? expense.category.id.eq(categoryId) : null))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1);

        for (Sort.Order order : pageable.getSort()) {
            query.orderBy(new OrderSpecifier<>(order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC, expense.date));
        }

        OrderSpecifier<?> createdAtOrder = new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, expense.createdAt);
        query.orderBy(createdAtOrder);
        List<Expense> results = query.fetch();

        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            results.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
