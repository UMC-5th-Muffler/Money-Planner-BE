package com.umc5th.muffler.domain.expense.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.umc5th.muffler.entity.QCategory.category;
import static com.umc5th.muffler.entity.QExpense.expense;

@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<LocalDate, List<Expense>> findByMemberAndDateRangeGroupedByDate(String memberId, LocalDate startDate, LocalDate endDate) {

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
    public Slice<Expense> findAllByMemberAndDate(String memberId, LocalDate date, Long lastExpenseId, Pageable pageable) {

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
    public Slice<Expense> findAllByMemberAndDateAndCategoryId(String memberId, LocalDate lastDate, Long lastExpenseId, LocalDate startDate, LocalDate endDate, Long categoryId, String order, int size) {
        Sort.Direction direction = order.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "date");

        JPAQuery<Expense> query = queryFactory
                .selectFrom(expense)
                .leftJoin(expense.category, category).fetchJoin()
                .where(expense.member.id.eq(memberId)
                        .and(expense.date.between(startDate, endDate))
                        .and(categoryId != null ? expense.category.id.eq(categoryId) : null))
                .limit(size + 1);

        boolean isAscending = direction.isAscending();
        if (lastDate != null && lastExpenseId != null) {
            if (isAscending) {
                query.where(expense.date.gt(lastDate)
                        .or(expense.date.eq(lastDate).and(expense.id.lt(lastExpenseId))));
            } else {
                query.where(expense.date.lt(lastDate)
                        .or(expense.date.eq(lastDate).and(expense.id.lt(lastExpenseId))));
            }
        }
        query.orderBy(new OrderSpecifier<>(isAscending ? Order.ASC : Order.DESC, expense.date));
        query.orderBy(expense.id.desc());
        List<Expense> results = query.fetch();

        boolean hasNext = false;
        if (results.size() > size) {
            results.remove(size);
            hasNext = true;
        }

        Pageable pageable = PageRequest.of(0, size, sort);
        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Long sumCategoryExpenseWithinGoal(String memberId, Category category, Goal goal) {
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
        Long sum = queryFactory
                .select(expense.cost.sum())
                .from(expense)
                .where(expense.member.id.eq(memberId)
                        .and(expense.date.between(startDate, endDate))
                ).fetchOne();
        return sum == null ? 0L : sum;
    }

    @Override
    public Slice<Expense> findByMemberAndTitleContaining(String memberId, String searchKeyword, LocalDate lastDate, Long lastExpenseId, int size, String order){
        Sort.Direction direction = order.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "date");

        JPAQuery<Expense> query = queryFactory
                .selectFrom(expense)
                .leftJoin(expense.category, category).fetchJoin()
                .where(expense.member.id.eq(memberId)
                        .and(searchTitle(searchKeyword)))
                .limit(size + 1);

        boolean isAscending = direction.isAscending();

        if (lastDate != null && lastExpenseId != null){
            if(isAscending){
                query.where(expense.date.gt(lastDate)
                        .or(expense.date.eq(lastDate).and(expense.id.lt(lastExpenseId))));
            } else {
                query.where(expense.date.lt(lastDate)
                        .or(expense.date.eq(lastDate).and(expense.id.lt(lastExpenseId))));
            }
        }
        query.orderBy(new OrderSpecifier<>(isAscending ? Order.ASC : Order.DESC, expense.date));
        query.orderBy(expense.id.desc());
        List<Expense> results = query.fetch();

        boolean hasNext = false;
        if (results.size() > size) {
            results.remove(size);
            hasNext = true;
        }

        Pageable pageable = PageRequest.of(0, size, sort);
        return new SliceImpl<>(results, pageable, hasNext);
    }

    private BooleanExpression searchTitle(String searchKeyword){
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            return expense.title.containsIgnoreCase(searchKeyword);
        }
        return null;
    }
}
