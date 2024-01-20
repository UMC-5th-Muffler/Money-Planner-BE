package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final GoalRepository goalRepository;

    public DailyExpenseResponse getDailyExpenseDetails(LocalDate date, Pageable pageable){
        Long memberId = 1L; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByDateBetween(date, memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode._NO_GOAL_IN_GIVEN_DATE));
        List<DailyPlan> dailyPlans = Optional.ofNullable(goal.getDailyPlans())
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));
        DailyPlan dailyPlan = findDailyPlan(dailyPlans, date);

        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDate(member, date, pageable);
        List<Category> categoryList = member.getCategories();

        DailyExpenseResponse response = ExpenseConverter.toDailyExpenseDetailsList(expenseList, categoryList, date, dailyPlan);

        return response;
    }

    public WeeklyExpenseResponse getWeeklyExpenseDetails(LocalDate date, Pageable pageable){
        Long memberId = 1L; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByDateBetween(date, memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode._NO_GOAL_IN_GIVEN_DATE));
        List<DailyPlan> dailyPlans = Optional.ofNullable(goal.getDailyPlans())
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));

        LocalDate startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // 해당 주 월요일 날짜
        LocalDate endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)); // 해당 주 일요일 날짜

        Long weeklyTotalCost = Optional.ofNullable(
                expenseRepository.calculateTotalCostByMemberAndDateBetween(member, startDate, endDate))
                .orElse(0L);
        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDateBetween(member, startDate, endDate, pageable);
        List<Category> categoryList = member.getCategories();

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.stream().collect(Collectors.groupingBy(Expense::getDate));
        Map<LocalDate, Long> dailyTotalCostMap = expensesByDate.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> findDailyPlan(dailyPlans, entry.getKey()).getTotalCost()
                ));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toDailyExpenseDetailsList(expensesByDate, dailyTotalCostMap);
        WeeklyExpenseResponse response = ExpenseConverter.toWeeklyExpenseDetailsResponse(dailyExpensesDtos, expenseList, categoryList, startDate, endDate, weeklyTotalCost);

        return response;
    }

    @Transactional
    public NewExpenseResponse enrollExpense(NewExpenseRequest request) {
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findCategoryWithNameAndMemberId(request.getCategoryName(), member.getId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        Goal goal = goalRepository.findByDateBetween(request.getExpenseDate(), request.getUserId())
                .orElseThrow(() -> new ExpenseException(ErrorCode._NO_GOAL_IN_GIVEN_DATE));

        Expense expense = ExpenseConverter.toExpenseEntity(request, member, category);
        expense = expenseRepository.save(expense);
        return new NewExpenseResponse(expense.getId(), expense.getCost());
    }

    private DailyPlan findDailyPlan(List<DailyPlan> dailyPlans, LocalDate date) {
        return dailyPlans.stream()
                .filter(dailyPlan -> dailyPlan.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));
    }
}
