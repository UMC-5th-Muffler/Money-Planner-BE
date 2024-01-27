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
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
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

    public OtherGoalsResponse getNoGoalTurnPage(String memberId, Integer year, Integer month) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        List<Goal> goalList = findGoalsByMonth(memberId, year, month);

        if (goalList.isEmpty()) {
            return new OtherGoalsResponse();
        }
        OtherGoalsResponse otherGoalsInfo = goalList.isEmpty() ? null : HomeConverter.toOtherGoals(goalList, year, month);
        return otherGoalsInfo;
    }

    public WholeCalendarResponse getGoalCalendarInfos(String memberId, Long goalId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalException(ErrorCode.GOAL_NOT_FOUND));

        LocalDate startDate = goal.getStartDate();
        LocalDate endDate = adjustEndDate(startDate.getYear(), startDate.getMonthValue(), goal.getEndDate());

        List<Goal> otherGoals = findOtherGoalsByMonth(memberId, goal, startDate);

        OtherGoalsResponse otherGoalsInfoList = otherGoals.isEmpty() ? null : HomeConverter.toOtherGoals(otherGoals, startDate.getYear(), startDate.getMonthValue());
        return process(goal, member, startDate, endDate, otherGoalsInfoList);
    }

    public WholeCalendarResponse getGoalTurnPage(String memberId, Long goalId, Integer year, Integer month) {
        return generateCalendarResponse(memberId, goalId, year, month);
    }

    private WholeCalendarResponse generateCalendarResponse(String memberId, Long goalId, Integer year, Integer month) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        List<Goal> goalList = findGoalsByMonth(memberId, year, month);

        if (goalList.isEmpty()) {
            return new WholeCalendarResponse();
        }
        Optional<Goal> goalWithinDate = findGoal(goalList, goalId, LocalDate.now());
        List<Goal> otherGoals = excludeGoalFromList(goalList, goalWithinDate.orElse(null));
        OtherGoalsResponse otherGoalsInfoList = otherGoals.isEmpty() ? null : HomeConverter.toOtherGoals(otherGoals, year, month);

        if (!goalWithinDate.isPresent()) {
            return HomeConverter.otherToWholeCalendar(otherGoalsInfoList);
        }

        Goal goal = goalWithinDate.get();
        LocalDate startDate = adjustStartDate(year, month, goal.getStartDate());
        LocalDate endDate = adjustEndDate(year, month, goal.getEndDate());
        return process(goal, member, startDate, endDate, otherGoalsInfoList);
    }

    private List<Goal> findGoalsByMonth(String memberId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        return goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)
                .orElse(Collections.emptyList());
    }

    private Optional<Goal> findGoal(List<Goal> goalList, Long goalId, LocalDate date) {
        return (goalId == null) ?
                goalList.stream()
                        .filter(goal -> !date.isBefore(goal.getStartDate()) && !date.isAfter(goal.getEndDate()))
                        .findFirst() :
                goalList.stream().filter(goal -> goal.getId().equals(goalId)).findFirst();
    }

    private List<Goal> excludeGoalFromList(List<Goal> goalList, Goal excludeGoal) {
        if (excludeGoal == null) {
            return goalList;
        }
        return goalList.stream()
                .filter(goal -> !goal.equals(excludeGoal))
                .collect(Collectors.toList());
    }

    private List<Goal> findOtherGoalsByMonth(String memberId, Goal goal, LocalDate startDate) {
        return goalRepository.findGoalsByMonth(startDate.withDayOfMonth(1), startDate.withDayOfMonth(startDate.lengthOfMonth()), memberId)
                .orElse(Collections.emptyList())
                .stream()
                .filter(otherGoal -> !otherGoal.equals(goal))
                .collect(Collectors.toList());
    }

    private WholeCalendarResponse process(Goal goal, Member member, LocalDate startDate, LocalDate endDate, OtherGoalsResponse otherGoalsInfoList) {
        List<Expense> expensesAll = expenseRepository.findAllByMemberAndDateBetween(member, goal.getStartDate(), goal.getEndDate());
        Long totalCost = calculateTotalCost(expensesAll);

        List<DailyPlan> relevantDailyPlans = filterDailyPlansByDate(goal.getDailyPlans(), startDate, endDate);
        List<WholeCalendarDailyInfo> dailyInfoList = HomeConverter.convertDailyPlansToDailyInfo(relevantDailyPlans);

        List<CategoryCalendarInfo> categoryInfoList = getCategoryInfo(goal, startDate, endDate, expensesAll);

        return HomeConverter.toWholeCalendar(goal, startDate, endDate, totalCost, dailyInfoList, categoryInfoList, otherGoalsInfoList);
    }

    private List<DailyPlan> filterDailyPlansByDate(List<DailyPlan> dailyPlans, LocalDate startDate, LocalDate endDate) {
        return dailyPlans.stream()
                .filter(dailyPlan -> !dailyPlan.getDate().isBefore(startDate) && !dailyPlan.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    private List<CategoryCalendarInfo> getCategoryInfo(Goal goal, LocalDate startDate, LocalDate endDate, List<Expense> expenseList) {

        List<Category> expenseCategoryList = expenseList.stream().map(Expense::getCategory).distinct().collect(Collectors.toList());
        Map<Category, Long> expenseCategoryMap = new HashMap<>();
        expenseCategoryList.forEach(category -> expenseCategoryMap.put(category, null));

        Map<Category, Long> goalCategoryMap = goal.getCategoryGoals().stream()
                .collect(Collectors.toMap(CategoryGoal::getCategory, CategoryGoal::getBudget));

        expenseCategoryMap.keySet().removeAll(goalCategoryMap.keySet());
        if(!expenseCategoryMap.isEmpty()) {
            goalCategoryMap.putAll(expenseCategoryMap);
        }

        goalCategoryMap.entrySet().removeIf(entry -> entry.getKey().getStatus() == Status.INACTIVE || entry.getKey().getIsVisible() == false);

        return goalCategoryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Category::getPriority)))
                .map(entry -> createCategoryCalendarInfo(entry.getKey(), entry.getValue(), startDate, endDate, expenseList))
                .collect(Collectors.toList());
    }

    private CategoryCalendarInfo createCategoryCalendarInfo(Category category, Long budget, LocalDate startDate, LocalDate endDate, List<Expense> expenseList) {
        List<Expense> expensesByCategory = expenseList.stream()
                .filter(expense -> expense.getCategory().equals(category))
                .collect(Collectors.toList());

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