package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Status;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseConverter {

    public static Expense toExpenseEntity(NewExpenseRequest request, Member member, Category category) {
        return Expense.builder()
            .title(request.getExpenseTitle())
            .memo(request.getExpenseMemo())
            .date(request.getExpenseDate())
            .cost(request.getExpenseCost())
            .category(category)
            .member(member)
            .build();
    }

    public static DailyExpenseResponse toDailyExpenseDetailsList(Slice<Expense> expenseList, List<Category> categoryList, LocalDate date, DailyPlan dailyPlan) {
        List<ExpenseDetailDto> expenseDetails = expenseList
                .stream()
                .map(expense -> ExpenseDetailDto.builder()
                        .expenseId(expense.getId())
                        .title(expense.getTitle())
                        .categoryIcon(expense.getCategory().getIcon())
                        .categoryId(expense.getCategory().getId())
                        .cost(expense.getCost())
                        .build())
                .collect(Collectors.toList());

        List<CategoryDetailDto> categoryDetails = categoryList
                .stream()
                .filter(category -> category.getStatus() == Status.ACTIVE)
                .filter(Category::getIsVisible)
                .sorted(Comparator.comparingLong(Category::getPriority))
                .map(category -> CategoryDetailDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());

        return DailyExpenseResponse.builder()
                .dailyTotalCost(dailyPlan.getTotalCost())
                .date(date)
                .isZeroDay(dailyPlan.getIsZeroDay())
                .expenseDetailDtoList(expenseDetails)
                .categoryList(categoryDetails)
                .hasNext(expenseList.hasNext())
                .build();
    }

    public static WeeklyExpenseResponse toWeeklyExpenseDetailsResponse(List<DailyExpensesDto> dailyExpensesDtos, Slice<Expense> expenseList,
                                                                       List<Category> categoryList, LocalDate startDate, LocalDate endDate, Long weeklyTotalCost){
        List<CategoryDetailDto> categoryDetails = categoryList
                .stream()
                .filter(category -> category.getStatus() == Status.ACTIVE)
                .filter(Category::getIsVisible)
                .sorted(Comparator.comparingLong(Category::getPriority))
                .map(category -> CategoryDetailDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());

        return WeeklyExpenseResponse.builder()
                .weeklyTotalCost(weeklyTotalCost)
                .startDate(startDate)
                .endDate(endDate)
                .categoryList(categoryDetails)
                .dailyExpenseList(dailyExpensesDtos)
                .hasNext(expenseList.hasNext())
                .build();
    }

    public static List<DailyExpensesDto> toDailyExpenseDetailsList(Map<LocalDate, List<Expense>> expensesByDate, Map<LocalDate, Long> dailyTotalCostMap) {

        return expensesByDate.entrySet().stream().map(entry -> {
            LocalDate dailyDate = entry.getKey();
            List<Expense> dailyExpenseList = entry.getValue();
            Long dailyTotalCost = dailyTotalCostMap.getOrDefault(dailyDate, 0L);

            List<ExpenseDetailDto> expenseDetailDtos = dailyExpenseList.stream()
                    .map(expense -> ExpenseDetailDto.builder()
                            .expenseId(expense.getId())
                            .title(expense.getTitle())
                            .categoryIcon(expense.getCategory().getIcon())
                            .categoryId(expense.getCategory().getId())
                            .cost(expense.getCost())
                            .build())
                    .collect(Collectors.toList());

            return DailyExpensesDto.builder()
                    .date(dailyDate)
                    .dailyTotalCost(dailyTotalCost)
                    .expenseDetailDtoList(expenseDetailDtos)
                    .build();
        }).collect(Collectors.toList());
    }

}
