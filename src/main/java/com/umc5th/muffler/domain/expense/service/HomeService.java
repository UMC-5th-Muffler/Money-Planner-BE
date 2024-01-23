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
        int year = date.getYear();
        int month = date.getMonthValue();

        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        Optional<List<Goal>> goalOpList = goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId);

        if (!goalOpList.isPresent()) {
            return new WholeCalendarResponse();
        }

        List<Goal> goalList = goalOpList.get();
        Optional<Goal> goalWithinDate = goalList.stream()
                .filter(goal -> !date.isBefore(goal.getStartDate()) && !date.isAfter(goal.getEndDate()))
                .findFirst();
        List<Goal> otherGoals = goalList.stream()
                .filter(goal -> date.isBefore(goal.getStartDate()) || date.isAfter(goal.getEndDate()))
                .collect(Collectors.toList());

        Goal goal = goalWithinDate.orElse(null);

        LocalDate goalStartDate = goal.getStartDate(); LocalDate goalEndDate = goal.getEndDate();
        LocalDate startDate = adjustStartDate(year, month, goalStartDate);
        LocalDate endDate = adjustEndDate(year, month, goalEndDate);

        List<OtherGoalsInfo> otherGoalsInfoList = new ArrayList<>();
        if (!otherGoals.isEmpty()) {
            for (Goal otherGoal : otherGoals) {
                LocalDate otherStartDate = adjustStartDate(year, month, otherGoal.getStartDate());
                LocalDate otherEndDate = adjustEndDate(year, month, otherGoal.getEndDate());

                List<Level> dailyRate = extractRates(otherGoal, otherStartDate, otherEndDate);

                OtherGoalsInfo otherGoalsInfo = OtherGoalsInfo.builder()
                        .otherStartDate(otherStartDate)
                        .otherEndDate(otherEndDate)
                        .totalLevelList(dailyRate)
                        .build();
                otherGoalsInfoList.add(otherGoalsInfo);
            }
        }

        return process(goal, member, startDate, endDate, otherGoalsInfoList);
    }

    public WholeCalendarResponse getGoalCalendarInfos(Long goalId, String memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalException(ErrorCode.GOAL_NOT_FOUND));

        LocalDate startDate = goal.getStartDate();
        LocalDate endDate = adjustEndDate(startDate.getYear(), startDate.getMonthValue(), goal.getEndDate());
        List<OtherGoalsInfo> otherGoalsInfoList = new ArrayList<>();
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