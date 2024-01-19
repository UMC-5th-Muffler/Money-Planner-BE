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

    public WholeCalendarResponse getWholeCalendarInfos(LocalDate date, Integer month) {

        WholeCalendarResponse response;

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new HomeException(ErrorCode.MEMBER_NOT_FOUND));
        Optional<Goal> goal = goalRepository.findByDateBetween(date, memberId);

        if (goal.isPresent()) {
            Goal actualGoal = goal.get();
            LocalDate startDate = adjustStartDate(month, actualGoal.getStartDate());
            LocalDate endDate = adjustEndDate(month, actualGoal.getEndDate());

            // 목표 기간 내의 모든 소비 리스트
            List<Expense> expensesAll = expenseRepository.findAllByMemberAndDateBetween(member, startDate, endDate);
            Long totalCost = expensesAll.stream()
                    .mapToLong(Expense::getCost)
                    .sum();

            // dailyList
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

            // 소비가 진행된 카테고리 리스트
            List<Category> expenseCategoryList = expenseRepository.findDistinctCategoriesBetweenDates(member, startDate, endDate);

            // 소비 진행 카테고리 맵
            Map<Category, Long> expenseCategoryMap = new HashMap<>();
            expenseCategoryList.forEach(category -> expenseCategoryMap.put(category, null));
            // 목표 있는 카테고리 맵 <Category, Budget>
            Map<Category, Long> goalCategoryMap = actualGoal.getCategoryGoals().stream()
                    .collect(Collectors.toMap(CategoryGoal::getCategory, CategoryGoal::getBudget));

            expenseCategoryMap.keySet().removeAll(goalCategoryMap.keySet());
            if(expenseCategoryMap != null) {
                // 목표 없지만 소비 있는 카테고리 존재 -> 합치기
                goalCategoryMap.putAll(expenseCategoryMap);
            }

            Map<Category, Long> sortedCategoryMap = new LinkedHashMap<>();
            goalCategoryMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(Category::getPriority)))
                    .forEach(entry -> sortedCategoryMap.put(entry.getKey(), entry.getValue()));

            List<CategoryCalendarInfo> categoryInfoList = new ArrayList<>();
            sortedCategoryMap.forEach((category, value) -> {
                if (value != null) {
                    System.out.println("value != 0L");
                    // categoryGoalList
                    // category goal 있는 것들
                    // id, name, categoryBudget, categoryTotalCost, categorySummary[dailyTotalCost, categoryRate]
                    List<Expense> expenses1 = expenseRepository.findAllByMemberAndCategoryIdAndDateBetween(member, category.getId(), startDate, endDate);

                    Long categoryTotalCost = expenses1.stream()
                            .mapToLong(Expense::getCost)
                            .sum();

                    List<Long> dailyTotalCostList1 = calculateDailyTotalCostList(expenses1, startDate, endDate);

                    List<DailyCategoryInfoDto> dailyCategoryInfoList = new ArrayList<>();
                    for (int i = 0; i < dailyTotalCostList1.size(); i++) {
                        DailyCategoryInfoDto dailyDto = DailyCategoryInfoDto.builder()
                                .dailyTotalCost(dailyTotalCostList1.get(i))
                                .dailyRate(Level.HIGH) // 임시
                                .build();

                        dailyCategoryInfoList.add(dailyDto);
                    }

                    CategoryCalendarInfo special = CategoryCalendarInfo.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .categoryBudget(value)
                            .categoryTotalCost(categoryTotalCost)
                            .categoryGoalSummary(dailyCategoryInfoList)
                            .build();

                    categoryInfoList.add(special);
                } else {
                    System.out.println("value == 0L");
                    // categoryNoGoalList
                    // category goal 없는 것들
                    // id, name, categoryTotalCost, dailyTotalCost

                    List<Expense> expenses1 = expenseRepository.findAllByMemberAndCategoryIdAndDateBetween(member, category.getId(), startDate, endDate);

                    Long categoryTotalCost = expenses1.stream()
                            .mapToLong(Expense::getCost)
                            .sum();

                    List<Long> dailyTotalCostList1 = calculateDailyTotalCostList(expenses1, startDate, endDate);
                    CategoryCalendarInfo dto = CategoryCalendarInfo.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .categoryTotalCost(categoryTotalCost)
                            .noGoalDailyTotalCost(dailyTotalCostList1)
                            .build();
                    categoryInfoList.add(dto);
                }
            });
            response = HomeConverter.toWholeCalendar(date, actualGoal, startDate, endDate, totalCost, dailyBudgetList, dailyTotalCostList, isZeroDayList, categoryInfoList);

        } else {
            response = new WholeCalendarResponse();
        }
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

    private LocalDate adjustStartDate(Integer month, LocalDate startDate) {
        if (startDate.getMonth().getValue() != month) {
            return YearMonth.of(startDate.getYear(), month).atDay(1);
        }
        return startDate;
    }

    private LocalDate adjustEndDate(Integer month, LocalDate endDate) {
        if (endDate.getMonth().getValue() != month) {
            return YearMonth.of(endDate.getYear(), month).atEndOfMonth();
        }
        return endDate;
    }
}
