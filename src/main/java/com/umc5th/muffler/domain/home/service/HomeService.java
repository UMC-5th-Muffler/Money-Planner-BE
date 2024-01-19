package com.umc5th.muffler.domain.home.service;

import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.home.dto.*;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.HomeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final ExpenseRepository expenseRepository;

    public WholeCalendarResponse getWholeCalendarInfos(LocalDate date, Integer year, Integer month) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        Optional<Goal> goal = goalRepository.findByDateBetween(date, memberId);


        if (!goal.isPresent()) {
            return new WholeCalendarResponse();
        }

        Goal actualGoal = goal.get();
        LocalDate goalStartDate = actualGoal.getStartDate();
        LocalDate goalEndDate = actualGoal.getEndDate();
        LocalDate startDate = adjustStartDate(year, month, goalStartDate);
        LocalDate endDate = adjustEndDate(year, month, goalEndDate);

        if ((startDate.isAfter(goalEndDate) || endDate.isBefore(goalStartDate))) {
            return new WholeCalendarResponse();
        }

        List<Expense> expensesAll = expenseRepository.findAllByMemberAndDateBetween(member, startDate, endDate);
        Long totalCost = calculateTotalCost(expensesAll);

        List<Long> dailyBudgetList = extractDailyBudgets(actualGoal);
        List<Long> dailyTotalCostList = extractDailyTotalCosts(actualGoal);
        List<Boolean> isZeroDayList = extractIsZeroDays(actualGoal);
        // TODO: List<Level> dailyRate 추가

        List<CategoryCalendarInfo> categoryInfoList = getCategoryInfo(actualGoal, member, startDate, endDate);

        return HomeConverter.toWholeCalendar(date, actualGoal, startDate, endDate, totalCost, dailyBudgetList, dailyTotalCostList, isZeroDayList, categoryInfoList);
    }

    private List<CategoryCalendarInfo> getCategoryInfo(Goal goal, Member member, LocalDate startDate, LocalDate endDate) {

        List<Category> expenseCategoryList = expenseRepository.findDistinctCategoriesBetweenDates(member, startDate, endDate);
        Map<Category, Long> expenseCategoryMap = new HashMap<>();
        expenseCategoryList.forEach(category -> expenseCategoryMap.put(category, null));
        Map<Category, Long> goalCategoryMap = goal.getCategoryGoals().stream()
                .collect(Collectors.toMap(CategoryGoal::getCategory, CategoryGoal::getBudget));

        expenseCategoryMap.keySet().removeAll(goalCategoryMap.keySet());
        if(expenseCategoryMap != null) {
            goalCategoryMap.putAll(expenseCategoryMap);
        }

        Map<Category, Long> sortedCategoryMap = new LinkedHashMap<>();
        goalCategoryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Category::getPriority)))
                .forEach(entry -> sortedCategoryMap.put(entry.getKey(), entry.getValue()));

        List<CategoryCalendarInfo> categoryInfoList = new ArrayList<>();
        sortedCategoryMap.forEach((category, value) -> {

            List<Expense> expensesByCategory = expenseRepository.findAllByMemberAndCategoryIdAndDateBetween(member, category.getId(), startDate, endDate);
            Long categoryTotalCost = calculateTotalCost(expensesByCategory);
            List<Long> dailyCategoryTotalCostList = calculateDailyTotalCostList(expensesByCategory, startDate, endDate);

            if (value != null) {
                // TODO: dailyCategoryRateList 추가

                List<DailyCategoryInfoDto> dailyCategoryInfoList = new ArrayList<>();
                for (int i = 0; i < dailyCategoryTotalCostList.size(); i++) {
//                    Level dailyRate = i < dailyRateList.size() ? dailyRateList.get(i) : null;
                    DailyCategoryInfoDto dailyDto = new DailyCategoryInfoDto(dailyCategoryTotalCostList.get(i), Level.HIGH);
                    dailyCategoryInfoList.add(dailyDto);
                }

                CategoryCalendarInfo dto = new CategoryCalendarInfo(category.getId(), category.getName(), value,
                        categoryTotalCost, dailyCategoryInfoList, null);

                categoryInfoList.add(dto);

            } else {
                CategoryCalendarInfo dto = new CategoryCalendarInfo(category.getId(), category.getName(), null,
                        categoryTotalCost, null, dailyCategoryTotalCostList);

                categoryInfoList.add(dto);
            }
        });

        return categoryInfoList;
    }

    private Long calculateTotalCost(List<Expense> expenses) {
        return expenses.stream()
                .mapToLong(Expense::getCost)
                .sum();
    }
    private List<Long> extractDailyBudgets(Goal goal) {
        return goal.getDailyPlans().stream()
                .map(DailyPlan::getBudget)
                .collect(Collectors.toList());
    }

    private List<Long> extractDailyTotalCosts(Goal goal) {
        return goal.getDailyPlans().stream()
                .map(DailyPlan::getTotalCost)
                .collect(Collectors.toList());
    }

    private List<Boolean> extractIsZeroDays(Goal goal) {
        return goal.getDailyPlans().stream()
                .map(DailyPlan::getIsZeroDay)
                .collect(Collectors.toList());
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