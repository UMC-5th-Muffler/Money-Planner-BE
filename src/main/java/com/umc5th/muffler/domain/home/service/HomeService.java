package com.umc5th.muffler.domain.home.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.home.dto.CategoryGoalCalendarResponse;
import com.umc5th.muffler.domain.home.dto.CategoryNoGoalCalendarResponse;
import com.umc5th.muffler.domain.home.dto.HomeConverter;
import com.umc5th.muffler.domain.home.dto.WholeCalendarResponse;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.HomeException;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryGoalRepository categoryGoalRepository;

    public WholeCalendarResponse getWholeCalendarInfos(LocalDate date) {

        WholeCalendarResponse response;

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Optional<Goal> goal = goalRepository.findByDateBetween(date, memberId);

        if (goal.isPresent()) {
            Goal actualGoal = goal.get();
            LocalDate startDate = actualGoal.getStartDate();
            LocalDate endDate = actualGoal.getEndDate();

            List<Expense> expenses = expenseRepository.findAllByMemberAndDateBetween(member, startDate, endDate);
            Long totalCost = expenses.stream()
                    .mapToLong(Expense::getCost)
                    .sum();

            List<Category> categoryExpenseList = expenseRepository.findDistinctCategoriesBetweenDates(member, startDate, endDate);
            Map<Category, Long> categoryMap = getCategoryMap(actualGoal, categoryExpenseList);

            List<DailyPlan> dailyPlanList = actualGoal.getDailyPlans();
            List<Long> dailyBudgetList = dailyPlanList.stream()
                    .map(DailyPlan::getBudget)
                    .collect(Collectors.toList());
            List<Long> dailyTotalCostList = dailyPlanList.stream()
                    .map(DailyPlan::getTotalCost)
                    .collect(Collectors.toList());
            List<Boolean> isZeroDayList = dailyPlanList.stream()
                    .map(DailyPlan::getIsZeroDay)
                    .collect(Collectors.toList());
            // TODO: List<Level> dailyRate 추가

            response = HomeConverter.toWholeCalendar(date, actualGoal, totalCost, categoryMap, dailyBudgetList, dailyTotalCostList, isZeroDayList);

        } else {
            response = new WholeCalendarResponse();
        }
        return response;
    }

    public CategoryGoalCalendarResponse getCategoryGoalInfos(Long goalId, Long categoryGoalId) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new HomeException(ErrorCode.GOAL_NOT_FOUND));
        CategoryGoal categoryGoal = categoryGoalRepository.findById(categoryGoalId).orElseThrow(() -> new HomeException(ErrorCode.CATEGORY_GOAL_NOT_FOUND));

        Long categoryBudget = categoryGoal.getBudget();
        Long categoryId = categoryGoal.getCategory().getId();
        List<Expense> expenses = expenseRepository.findAllByMemberAndCategoryIdAndDateBetween(member, categoryId, goal.getStartDate(), goal.getEndDate());

        Long categoryTotalCost = expenses.stream()
                .mapToLong(Expense::getCost)
                .sum();

        List<Long> dailyTotalCostList = calculateDailyTotalCostList(expenses, goal.getStartDate(), goal.getEndDate());
        // TODO: List<Level> dailyRate 추가

        CategoryGoalCalendarResponse response = HomeConverter.toGoalCalendar(categoryBudget, categoryTotalCost, dailyTotalCostList);

        return response;
    }

    public CategoryNoGoalCalendarResponse getCategoryNoGoalInfos(Long goalId, Long categoryId) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new HomeException(ErrorCode.GOAL_NOT_FOUND));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new HomeException(ErrorCode.CATEGORY_NOT_FOUND));

        List<Expense> expenses = expenseRepository.findAllByMemberAndCategoryIdAndDateBetween(member, categoryId, goal.getStartDate(), goal.getEndDate());
        List<Long> dailyTotalCostList = calculateDailyTotalCostList(expenses, goal.getStartDate(), goal.getEndDate());

        CategoryNoGoalCalendarResponse response = HomeConverter.toNoGoalCalendar(dailyTotalCostList);

        return response;
    }

    private List<Long> calculateDailyTotalCostList(List<Expense> expenses, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Long> dailyExpenseMap = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getDate, Collectors.summingLong(Expense::getCost)));

        List<Long> dailyTotalCostList = startDate.datesUntil(endDate.plusDays(1))
                .map(d -> dailyExpenseMap.getOrDefault(d, 0L))
                .collect(Collectors.toList());

        return dailyTotalCostList;
    }

    private Map<Category, Long> getCategoryMap(Goal goal, List<Category> categoryList) {
        Map<Category, Long> categoryGoalMap = goal.getCategoryGoals().stream()
                .collect(Collectors.toMap(CategoryGoal::getCategory, CategoryGoal::getId));

        List<Category> mergedList = new ArrayList<>(categoryList);
        mergedList.addAll(categoryGoalMap.keySet());
        mergedList.sort(Comparator.comparing(Category::getPriority));

        Map<Category, Long> categoryMap = new LinkedHashMap<>();
        for (Category category : mergedList) {
            categoryMap.put(category, categoryGoalMap.getOrDefault(category, null));
        }

        return categoryMap;
    }
}
