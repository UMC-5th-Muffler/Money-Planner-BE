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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
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

        DailyExpenseResponse response = ExpenseConverter.toDailyExpenseDetailsList(date, expenseList, dailyPlan, rate);

        return response;
    }

    public WeeklyExpenseResponse getWeeklyExpenseDetails(String memberId, LocalDate date, Pageable pageable){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByDateBetween(date, memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_GOAL_IN_GIVEN_DATE));
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


    public MonthlyExpenseResponse getHomeMonthlyExpenses(String memberId, Integer year, Integer month, Long goalId, String order, Pageable pageable){
        if (goalId == null){
            return new MonthlyExpenseResponse();
        }
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.GOAL_NOT_FOUND));

        LocalDate startDate = getStartDate(goal, year, month);
        LocalDate endDate = getEndDate(goal, year, month);
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

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toMonthlyDailyExpenseDetailsList(expensesByDate, dailyTotalCostMap, order);
        return ExpenseConverter.toMonthlyExpensesResponse(dailyExpensesDtos, expenseList);
    }

    public MonthlyExpenseResponse getHomeExpensesWithCategory(String memberId, Integer year, Integer month, Long goalId, Long categoryId, String order, Pageable pageable){
        if (goalId == null){
            return new MonthlyExpenseResponse();
        }
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.GOAL_NOT_FOUND));

        LocalDate startDate = getStartDate(goal, year, month);
        LocalDate endDate = getEndDate(goal, year, month);
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

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toMonthlyDailyExpenseDetailsList(expensesByDate, order);
        return ExpenseConverter.toMonthlyExpensesResponse(dailyExpensesDtos, expenseList);
    }


    private DailyPlan findDailyPlan(List<DailyPlan> dailyPlans, LocalDate date) {
        return dailyPlans.stream()
                .filter(dailyPlan -> dailyPlan.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new GoalException(ErrorCode.DAILYPLAN_NOT_FOUND));
    }

    private LocalDate getStartDate(Goal goal, int year, int month) {
        LocalDate startDate = YearMonth.of(year, month).atDay(1);
        return goal.getStartDate().isBefore(startDate) ? startDate : goal.getStartDate();
    }

    private LocalDate getEndDate(Goal goal, int year, int month) {
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        return goal.getEndDate().isAfter(endDate) ? endDate : goal.getEndDate();
    }
}
