package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.dto.homeDto.*;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.entity.constant.Status;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.HomeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final ExpenseRepository expenseRepository;

    public WholeCalendarResponse getWholeCalendarInfos(String memberId) {

        LocalDate date = LocalDate.now();
        Integer year = date.getYear(); Integer month = date.getMonthValue();
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        List<Goal> goalList = goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)
                .orElse(Collections.emptyList());

        if (goalList.isEmpty()) { // 아무런 목표도 없음
            return new WholeCalendarResponse();
        }

        Map<Boolean, List<Goal>> partitionGoals = goalList.stream()
                .collect(Collectors.partitioningBy(goal -> !date.isBefore(goal.getStartDate()) && !date.isAfter(goal.getEndDate())));
        Optional<Goal> goalWithinDate = partitionGoals.get(true).stream().findFirst();
        List<Goal> otherGoals = partitionGoals.get(false);
        List<OtherGoalsInfo> otherGoalsInfoList = createOtherGoalsInfoList(otherGoals, year, month);

        if (!goalWithinDate.isPresent()) { // 오늘 날짜에 해당되는 목표X, 다른 목표는 존재
            return HomeConverter.toOtherGoalsCalendar(otherGoalsInfoList);
        }
        Goal goal = goalWithinDate.get();
        LocalDate startDate = adjustStartDate(year, month, goal.getStartDate());
        LocalDate endDate = adjustEndDate(year, month, goal.getEndDate());

        return process(goal, member, startDate, endDate, otherGoalsInfoList);
    }

    public WholeCalendarResponse getGoalCalendarInfos(Long goalId, String memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalException(ErrorCode.GOAL_NOT_FOUND));

        LocalDate startDate = goal.getStartDate();
        LocalDate endDate = adjustEndDate(startDate.getYear(), startDate.getMonthValue(), goal.getEndDate());

        LocalDate startOfMonth = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = startDate.with(TemporalAdjusters.lastDayOfMonth());
        List<Goal> otherGoals = goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)
                .orElse(Collections.emptyList())
                .stream()
                .filter(otherGoal -> !otherGoal.equals(goal))
                .collect(Collectors.toList());

        List<OtherGoalsInfo> otherGoalsInfoList = createOtherGoalsInfoList(otherGoals, startDate.getYear(), startDate.getMonthValue());
        return process(goal, member, startDate, endDate, otherGoalsInfoList);
    }

    public WholeCalendarResponse getTurnPage(Long goalId, String memberId, Integer year, Integer month) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1); LocalDate endOfMonth = yearMonth.atEndOfMonth();
        List<Goal> goalList = goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)
                .orElse(Collections.emptyList());

        Optional<Goal> matchingGoal = goalList.stream()
                .filter(goal -> goal.getId().equals(goalId))
                .findFirst();
        List<Goal> otherGoals = goalList.stream()
                .filter(goal -> !goal.getId().equals(goalId))
                .collect(Collectors.toList());
        List<OtherGoalsInfo> otherGoalsInfoList = createOtherGoalsInfoList(otherGoals, year, month);

        if (!matchingGoal.isPresent()) {
            return HomeConverter.toOtherGoalsCalendar(otherGoalsInfoList);
        }
        Goal goal = matchingGoal.get();
        LocalDate startDate = adjustStartDate(year, month, goal.getStartDate());
        LocalDate endDate = adjustEndDate(year, month, goal.getEndDate());
        return process(goal, member, startDate, endDate, otherGoalsInfoList);
    }

    private WholeCalendarResponse process(Goal goal, Member member, LocalDate startDate, LocalDate endDate, List<OtherGoalsInfo> otherGoalsInfoList) {
        List<Expense> expensesAll = expenseRepository.findAllByMemberAndDateBetween(member, startDate, endDate);
        Long totalCost = calculateTotalCost(expensesAll);

        List<Long> dailyBudgetList = extractDailyBudgets(goal, startDate, endDate);
        List<Long> dailyTotalCostList = extractDailyTotalCosts(goal, startDate, endDate);
        List<Level> dailyRateList = extractRates(goal, startDate, endDate);
        List<Boolean> isZeroDayList = extractIsZeroDays(goal, startDate, endDate);
        nullifyBudgetToZeroCost(dailyTotalCostList, dailyBudgetList);

        List<CategoryCalendarInfo> categoryInfoList = getCategoryInfo(goal, member, startDate, endDate);

        return HomeConverter.toWholeCalendar(goal, startDate, endDate, totalCost, dailyBudgetList, dailyTotalCostList, dailyRateList, isZeroDayList, categoryInfoList, otherGoalsInfoList);
    }

    private List<OtherGoalsInfo> createOtherGoalsInfoList(List<Goal> otherGoals, Integer year, Integer month) {
        if (otherGoals.isEmpty()) {
            return null;
        }
        return otherGoals.stream()
                .map(otherGoal -> {
                    LocalDate otherStartDate = adjustStartDate(year, month, otherGoal.getStartDate());
                    LocalDate otherEndDate = adjustEndDate(year, month, otherGoal.getEndDate());
                    List<Level> dailyRate = extractRates(otherGoal, otherStartDate, otherEndDate);

                    return OtherGoalsInfo.builder()
                            .otherStartDate(otherStartDate)
                            .otherEndDate(otherEndDate)
                            .totalLevelList(dailyRate)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<CategoryCalendarInfo> getCategoryInfo(Goal goal, Member member, LocalDate startDate, LocalDate endDate) {

        List<Category> expenseCategoryList = expenseRepository.findDistinctCategoriesBetweenDates(member, startDate, endDate);
        Map<Category, Long> expenseCategoryMap = new HashMap<>();
        expenseCategoryList.forEach(category -> expenseCategoryMap.put(category, null));
        Map<Category, Long> goalCategoryMap = goal.getCategoryGoals().stream()
                .collect(Collectors.toMap(CategoryGoal::getCategory, CategoryGoal::getBudget));

        expenseCategoryMap.keySet().removeAll(goalCategoryMap.keySet());

        if(!expenseCategoryMap.isEmpty()) {
            goalCategoryMap.putAll(expenseCategoryMap);
        }

        goalCategoryMap.entrySet().removeIf(entry -> entry.getKey().getStatus() == Status.INACTIVE);

        expenseCategoryMap.keySet().removeAll(goalCategoryMap.keySet());
        goalCategoryMap.putAll(expenseCategoryMap);

        goalCategoryMap.entrySet().removeIf(entry -> entry.getKey().getStatus() == Status.INACTIVE);
        goalCategoryMap.entrySet().removeIf(entry -> entry.getKey().getIsVisible() == false);

        return goalCategoryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Category::getPriority)))
                .map(entry -> createCategoryCalendarInfo(member, entry.getKey(), entry.getValue(), startDate, endDate))
                .collect(Collectors.toList());
    }

    private CategoryCalendarInfo createCategoryCalendarInfo(Member member, Category category, Long budget, LocalDate startDate, LocalDate endDate) {
        List<Expense> expensesByCategory = expenseRepository.findAllByMemberAndCategoryIdAndDateBetween(member, category.getId(), startDate, endDate);
        Long categoryTotalCost = calculateTotalCost(expensesByCategory);
        List<Long> dailyCategoryTotalCostList = calculateDailyTotalCostList(expensesByCategory, startDate, endDate);

        return new CategoryCalendarInfo(category.getId(), category.getName(), budget,
                categoryTotalCost, dailyCategoryTotalCostList);
    }

    private Long calculateTotalCost(List<Expense> expenses) {
        return expenses.stream()
                .mapToLong(Expense::getCost)
                .sum();
    }
    private List<Long> extractDailyBudgets(Goal goal, LocalDate startDate, LocalDate endDate) {
        return goal.getDailyPlans().stream()
                .filter(dailyPlan ->
                        !dailyPlan.getDate().isBefore(startDate) &&
                        !dailyPlan.getDate().isAfter(endDate))
                .map(DailyPlan::getBudget)
                .collect(Collectors.toList());
    }

    private List<Long> extractDailyTotalCosts(Goal goal, LocalDate startDate, LocalDate endDate) {
        return goal.getDailyPlans().stream()
                .filter(dailyPlan ->
                        !dailyPlan.getDate().isBefore(startDate) &&
                        !dailyPlan.getDate().isAfter(endDate))
                .map(DailyPlan::getTotalCost)
                .collect(Collectors.toList());
    }

    private List<Level> extractRates(Goal goal, LocalDate startDate, LocalDate endDate) {
        return goal.getDailyPlans().stream()
                .filter(dailyPlan ->
                        !dailyPlan.getDate().isBefore(startDate) &&
                        !dailyPlan.getDate().isAfter(endDate))
                .map(DailyPlan::getRate)
                .map(rate -> (rate != null) ? rate.getTotalLevel() : null)
                .collect(Collectors.toList());
    }

    private List<Boolean> extractIsZeroDays(Goal goal, LocalDate startDate, LocalDate endDate) {
        return goal.getDailyPlans().stream()
                .filter(dailyPlan ->
                        !dailyPlan.getDate().isBefore(startDate) &&
                        !dailyPlan.getDate().isAfter(endDate))
                .map(DailyPlan::getIsZeroDay)
                .collect(Collectors.toList());
    }


    private void nullifyBudgetToZeroCost(List<Long> dailyTotalCostList, List<Long> dailyBudgetList) {
        if (dailyTotalCostList.size() != dailyBudgetList.size()) {
            throw new IllegalArgumentException("리스트의 크기가 서로 다릅니다.");
        }

        IntStream.range(0, dailyTotalCostList.size())
                .filter(i -> dailyTotalCostList.get(i) == 0)
                .forEach(i -> dailyBudgetList.set(i, null));
    }


    private List<Long> calculateDailyTotalCostList(List<Expense> expenses, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Long> dailyExpenseMap = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getDate, Collectors.summingLong(Expense::getCost)));

        List<Long> dailyTotalCostList = startDate.datesUntil(endDate.plusDays(1))
                .map(d -> dailyExpenseMap.getOrDefault(d, 0L))
                .collect(Collectors.toList());

        return dailyTotalCostList;
    }

    private LocalDate adjustStartDate(Integer year, Integer month, LocalDate startDate) {
        YearMonth yearMonth = YearMonth.of(year, month);
        if (startDate.isBefore(yearMonth.atDay(1))) {
            return yearMonth.atDay(1);
        }
        return startDate;
    }

    private LocalDate adjustEndDate(Integer year, Integer month, LocalDate endDate) {
        YearMonth yearMonth = YearMonth.of(year, month);
        if (endDate.isAfter(yearMonth.atEndOfMonth())) {
            return yearMonth.atEndOfMonth();
        }
        return endDate;
    }
}