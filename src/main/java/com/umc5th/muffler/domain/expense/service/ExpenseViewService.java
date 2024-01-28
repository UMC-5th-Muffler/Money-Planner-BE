package com.umc5th.muffler.domain.expense.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseViewService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;

    public ExpenseDto getExpense(Long expenseId){
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));

        return ExpenseConverter.toExpenseDto(expense);
    }

    public DailyExpenseResponse getDailyExpenseDetails(String memberId, LocalDate date, Pageable pageable){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByDateBetween(date, memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_GOAL_IN_GIVEN_DATE));
        List<DailyPlan> dailyPlans = Optional.ofNullable(goal.getDailyPlans())
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));
        DailyPlan dailyPlan = findDailyPlan(dailyPlans, date);
        Rate rate = dailyPlan.getRate();

        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDate(member, date, pageable);

        DailyExpenseResponse response = ExpenseConverter.toDailyExpensesList(date, expenseList, dailyPlan, rate);

        return response;
    }

    public WeeklyExpenseResponse getWeeklyExpenseDetails(String memberId, Long goalId, LocalDate weeklyStartDate, LocalDate weeklyEndDate, Pageable pageable){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalException(ErrorCode.NO_GOAL_IN_GIVEN_DATE));
        List<DailyPlan> dailyPlans = Optional.ofNullable(goal.getDailyPlans())
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));

        // 주간에 포함되고 목표와 관련된 expense 기간
        LocalDate expenseStartDate = goal.getStartDate().isBefore(weeklyStartDate) ? weeklyStartDate : goal.getStartDate();
        LocalDate expenseEndDate = goal.getEndDate().isAfter(weeklyEndDate) ? weeklyEndDate : goal.getEndDate();

        Specification<Expense> spec = Specification
                .where(ExpenseSpecification.hasMember(member))
                .and(ExpenseSpecification.isBetweenDates(expenseStartDate, expenseEndDate));
        Slice<Expense> expenseList = expenseRepository.findAll(spec, pageable);
        List<Category> categoryList = member.getCategories();

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.stream().collect(Collectors.groupingBy(Expense::getDate));
        Map<LocalDate, Long> dailyTotalCostMap = expensesByDate.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> findDailyPlan(dailyPlans, entry.getKey()).getTotalCost()
                ));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toDailyExpensesList(expensesByDate, dailyTotalCostMap);
        WeeklyExpenseResponse response = ExpenseConverter.toWeeklyExpensesResponse(dailyExpensesDtos, expenseList, categoryList);

        return response;
    }


    public MonthlyExpenseResponse getMonthlyExpenses(String memberId, YearMonth yearMonth, Long goalId, String order, Pageable pageable){
        if (goalId == null){
            return new MonthlyExpenseResponse();
        }
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.GOAL_NOT_FOUND));

        LocalDate startDate = getStartDate(goal, yearMonth);
        LocalDate endDate = getEndDate(goal, yearMonth);
        if(startDate.isAfter(endDate)){
            return new MonthlyExpenseResponse();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        List<DailyPlan> dailyPlans = Optional.ofNullable(goal.getDailyPlans())
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));

        Specification<Expense> spec = Specification
                .where(ExpenseSpecification.hasMember(member))
                .and(ExpenseSpecification.isBetweenDates(startDate, endDate));
        Slice<Expense> expenseList = expenseRepository.findAll(spec, pageable);

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.getContent().stream()
                .collect(Collectors.groupingBy(Expense::getDate));
        Map<LocalDate, Long> dailyTotalCostMap = expensesByDate.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> findDailyPlan(dailyPlans, entry.getKey()).getTotalCost()
                ));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toDailyExpensesListWithOrderAndTotalCost(expensesByDate, dailyTotalCostMap, order);
        return ExpenseConverter.toMonthlyExpensesResponse(dailyExpensesDtos, expenseList);
    }

    public MonthlyExpenseResponse getMonthlyExpensesWithCategory(String memberId, YearMonth yearMonth, Long goalId, Long categoryId, String order, Pageable pageable){
        if (goalId == null){
            return new MonthlyExpenseResponse();
        }
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.GOAL_NOT_FOUND));

        LocalDate startDate = getStartDate(goal, yearMonth);
        LocalDate endDate = getEndDate(goal, yearMonth);
        if(startDate.isAfter(endDate)){
            return new MonthlyExpenseResponse();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Specification<Expense> spec = Specification
                .where(ExpenseSpecification.hasMember(member))
                .and(ExpenseSpecification.isBetweenDates(startDate, endDate))
                .and(ExpenseSpecification.hasCategory(categoryId));
        Slice<Expense> expenseList = expenseRepository.findAll(spec, pageable);

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.getContent().stream()
                .collect(Collectors.groupingBy(Expense::getDate));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toDailyExpensesListWithOrderAndTotalCost(expensesByDate, order);
        return ExpenseConverter.toMonthlyExpensesResponse(dailyExpensesDtos, expenseList);
    }


    private DailyPlan findDailyPlan(List<DailyPlan> dailyPlans, LocalDate date) {
        return dailyPlans.stream()
                .filter(dailyPlan -> dailyPlan.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));
    }

    private LocalDate getStartDate(Goal goal, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        return goal.getStartDate().isBefore(startDate) ? startDate : goal.getStartDate();
    }

    private LocalDate getEndDate(Goal goal, YearMonth yearMonth) {
        LocalDate endDate = yearMonth.atEndOfMonth();
        return goal.getEndDate().isAfter(endDate) ? endDate : goal.getEndDate();
    }
}
