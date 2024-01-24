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
        return generateCalendarResponse(memberId, null, date.getYear(), date.getMonthValue());
    }

    public WholeCalendarResponse getGoalCalendarInfos(Long goalId, String memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalException(ErrorCode.GOAL_NOT_FOUND));
        return generateGoalCalendarResponse(member, goal);
    }

    public WholeCalendarResponse getTurnPage(Long goalId, String memberId, Integer year, Integer month) {
        return generateCalendarResponse(memberId, goalId, year, month);
    }

    private WholeCalendarResponse generateCalendarResponse(String memberId, Long goalId, Integer year, Integer month) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<Goal> goalList = goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)
                .orElse(Collections.emptyList());

        if (goalList.isEmpty()) {
            return new WholeCalendarResponse();
        }
        Optional<Goal> goalWithinDate = (goalId == null) ?
                findRelevantGoal(goalList, LocalDate.now()) :
                goalList.stream().filter(goal -> goal.getId().equals(goalId)).findFirst();

        List<Goal> otherGoals = excludeGoalFromList(goalList, goalWithinDate.orElse(null));
        List<OtherGoalsInfo> otherGoalsInfoList = createOtherGoalsInfoList(otherGoals, year, month);

        if (!goalWithinDate.isPresent()) {
            return HomeConverter.toOtherGoalsCalendar(otherGoalsInfoList);
        }

        Goal goal = goalWithinDate.get();
        LocalDate startDate = adjustStartDate(year, month, goal.getStartDate());
        LocalDate endDate = adjustEndDate(year, month, goal.getEndDate());
        return process(goal, member, startDate, endDate, otherGoalsInfoList);
    }

    private Optional<Goal> findRelevantGoal(List<Goal> goalList, LocalDate date) {
        return goalList.stream()
                .filter(goal -> !date.isBefore(goal.getStartDate()) && !date.isAfter(goal.getEndDate()))
                .findFirst();
    }

    private List<Goal> excludeGoalFromList(List<Goal> goalList, Goal excludeGoal) {
        if (excludeGoal == null) {
            return new ArrayList<>(goalList);
        }
        return goalList.stream()
                .filter(goal -> !goal.equals(excludeGoal))
                .collect(Collectors.toList());
    }

    private WholeCalendarResponse generateGoalCalendarResponse(Member member, Goal goal) {

        LocalDate startDate = goal.getStartDate();
        LocalDate endDate = adjustEndDate(startDate.getYear(), startDate.getMonthValue(), goal.getEndDate());
        YearMonth yearMonth = YearMonth.of(startDate.getYear(), startDate.getMonthValue());
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<Goal> otherGoals = goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, member.getId())
                .orElse(Collections.emptyList())
                .stream()
                .filter(otherGoal -> !otherGoal.equals(goal))
                .collect(Collectors.toList());

        List<OtherGoalsInfo> otherGoalsInfoList = createOtherGoalsInfoList(otherGoals, startDate.getYear(), startDate.getMonthValue());
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

        goalCategoryMap.entrySet().removeIf(entry -> entry.getKey().getStatus() == Status.INACTIVE || !entry.getKey().getIsVisible());

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