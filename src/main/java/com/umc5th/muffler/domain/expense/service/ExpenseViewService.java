package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.DailyPlanException;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseViewService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;

    public ExpenseDto getExpense(String memberId, Long expenseId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Expense expense = expenseRepository.findByIdJoin(expenseId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));

        return ExpenseConverter.toExpenseDto(expense);
    }

    public DailyExpenseResponse getDailyExpenseDetails(String memberId, LocalDate date, Long lastExpenseId, Pageable pageable){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDate(memberId, date, lastExpenseId, pageable);

        DailyExpenseResponse response = ExpenseConverter.toDailyExpensesListWithTotalCost(expenseList);
        return response;
    }

    public WeeklyExpenseResponse getWeeklyExpenseDetails(String memberId, Long goalId, LocalDate weeklyStartDate, LocalDate weeklyEndDate, LocalDate lastDate, Long lastExpenseId, int size){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalException(ErrorCode.NO_GOAL_IN_GIVEN_DATE));
        List<DailyPlan> dailyPlans = Optional.ofNullable(goal.getDailyPlans())
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));

        // 주간에 포함되고 목표와 관련된 expense 기간
        LocalDate expenseStartDate = goal.getStartDate().isBefore(weeklyStartDate) ? weeklyStartDate : goal.getStartDate();
        LocalDate expenseEndDate = goal.getEndDate().isAfter(weeklyEndDate) ? weeklyEndDate : goal.getEndDate();

        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDateAndCategoryId(member.getId(), lastDate, lastExpenseId, expenseStartDate, expenseEndDate, null, "DESC" ,size);

        // 일별로 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.getContent().stream()
                .collect(Collectors.groupingBy(Expense::getDate, LinkedHashMap::new, Collectors.toList()));
        Map<LocalDate, DailyPlan> dailyPlanByDate = dailyPlans.stream()
                .collect(Collectors.toMap(DailyPlan::getDate, Function.identity()));

        Map<LocalDate, Long> dailyTotalCostMap = expensesByDate.keySet().stream()
                .collect(Collectors.toMap(date -> date, date -> {
                    DailyPlan dailyPlan = dailyPlanByDate.get(date);
                    if (dailyPlan == null) {
                        throw new DailyPlanException(ErrorCode.DAILYPLAN_NOT_FOUND);
                    }
                    return dailyPlan.getTotalCost();
                }));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toDailyExpensesListWithTotalCost(expensesByDate, dailyTotalCostMap);
        WeeklyExpenseResponse response = ExpenseConverter.toWeeklyExpensesResponse(dailyExpensesDtos, expenseList.hasNext());

        return response;
    }


    public MonthlyExpenseResponse getMonthlyExpenses(String memberId, YearMonth yearMonth, Long goalId, String order, LocalDate lastDate, Long lastExpenseId, int size){
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

        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDateAndCategoryId(member.getId(), lastDate, lastExpenseId, startDate, endDate, null, order, size);

        // 일별로 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.getContent().stream()
                .collect(Collectors.groupingBy(Expense::getDate, LinkedHashMap::new, Collectors.toList()));
        Map<LocalDate, DailyPlan> dailyPlanByDate = dailyPlans.stream()
                .collect(Collectors.toMap(DailyPlan::getDate, Function.identity()));

        Map<LocalDate, Long> dailyTotalCostMap = expensesByDate.keySet().stream()
                .collect(Collectors.toMap(date -> date, date -> {
                    DailyPlan dailyPlan = dailyPlanByDate.get(date);
                    if (dailyPlan == null) {
                        throw new DailyPlanException(ErrorCode.DAILYPLAN_NOT_FOUND);
                    }
                    return dailyPlan.getTotalCost();
                }));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toDailyExpensesListWithTotalCost(expensesByDate, dailyTotalCostMap);
        return ExpenseConverter.toMonthlyExpensesResponse(dailyExpensesDtos, expenseList.hasNext());
    }

    public MonthlyExpenseResponse getMonthlyExpensesWithCategory(String memberId, YearMonth yearMonth, Long goalId, Long categoryId, String order, LocalDate lastDate, Long lastExpenseId, int size){
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

        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDateAndCategoryId(member.getId(), lastDate, lastExpenseId, startDate, endDate, categoryId, order, size);

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.getContent().stream()
                .collect(Collectors.groupingBy(Expense::getDate, LinkedHashMap::new, Collectors.toList()));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toDailyExpensesList(expensesByDate);
        return ExpenseConverter.toMonthlyExpensesResponse(dailyExpensesDtos, expenseList.hasNext());
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
