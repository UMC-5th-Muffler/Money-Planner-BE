package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseConverter {

    public static DailyExpenseDetailsResponse toDailyExpenseDetailsResponse(Slice<Expense> expenseList, List<Category> categoryList, LocalDate date, Long dailyTotalCost) {

        // Expense(entity) -> ExpenseDetail(dto)
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

        // Category(entity) -> CategoryDetail(dto)
        List<CategoryDetailDto> categoryDetails = categoryList
                .stream()
                .map(category -> CategoryDetailDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());

        return DailyExpenseDetailsResponse.builder()
                .dailyTotalCost(dailyTotalCost)
                .date(date)
                .expenseDetailDtoList(expenseDetails)
                .categoryList(categoryDetails)
                .hasNext(expenseList.hasNext())
                .build();
    }

    public static WeeklyExpenseDetailsResponse toWeeklyExpenseDetailsResponse(List<DailyExpenseDetailsDto> dailyExpenseDetailsDtos, Slice<Expense> expenseList,
                                                                              List<Category> categoryList, LocalDate startDate, LocalDate endDate, Long weeklyTotalCost){
        List<CategoryDetailDto> categoryDetails = categoryList
                .stream()
                .map(category -> CategoryDetailDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());

        return WeeklyExpenseDetailsResponse.builder()
                .weeklyTotalCost(weeklyTotalCost)
                .startDate(startDate)
                .endDate(endDate)
                .categoryList(categoryDetails)
                .dailyExpenseList(dailyExpenseDetailsDtos)
                .hasNext(expenseList.hasNext())
                .build();
    }

    public static List<DailyExpenseDetailsDto> toDailyExpenseDetailsResponse(Map<LocalDate, List<Expense>> expensesByDate, Map<LocalDate, Long> dailyTotalCostMap) {

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

            return DailyExpenseDetailsDto.builder()
                    .date(dailyDate)
                    .dailyTotalCost(dailyTotalCost)
                    .expenseDetailDtoList(expenseDetailDtos)
                    .build();
        }).collect(Collectors.toList());
    }
}
