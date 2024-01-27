package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.entity.constant.Status;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static DailyExpenseResponse toDailyExpenseDetailsList(LocalDate date, Slice<Expense> expenseList, DailyPlan dailyPlan, Rate rate) {
        List<ExpenseDetailDto> expenseDetails = toExpensesDetails(expenseList.getContent());

        return DailyExpenseResponse.builder()
                .dailyTotalCost(dailyPlan.getTotalCost())
                .date(date)
                .isZeroDay(dailyPlan.getIsZeroDay())
                .rateId((rate != null) ? rate.getId() : null)
                .rateLevel((rate != null) ? rate.getTotalLevel() : null)
                .rateMemo((rate != null) ? rate.getMemo() : null)
                .expenseDetailList(expenseDetails)
                .hasNext(expenseList.hasNext())
                .build();
    }

    public static WeeklyExpenseResponse toWeeklyExpenseDetailsResponse(List<DailyExpensesDto> dailyExpensesDtos, Slice<Expense> expenseList,
                                                                       List<Category> categoryList, LocalDate startDate, LocalDate endDate, Long weeklyTotalCost){
        List<CategoryDetailDto> categoryDetails = toCategoryDetails(categoryList);

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

            List<ExpenseDetailDto> expenseDetails = toExpensesDetails(dailyExpenseList);

            return DailyExpensesDto.builder()
                    .date(dailyDate)
                    .dailyTotalCost(dailyTotalCost)
                    .expenseDetailList(expenseDetails)
                    .build();
        }).collect(Collectors.toList());
    }

    public static List<DailyExpensesDto> toMonthlyDailyExpenseDetailsList(Map<LocalDate, List<Expense>> expensesByDate, Map<LocalDate, Long> dailyTotalCostMap, String order) {
        Stream<Map.Entry<LocalDate, List<Expense>>> stream = expensesByDate.entrySet().stream();

        if ("ASC".equalsIgnoreCase(order)) {
            stream = stream.sorted(Map.Entry.comparingByKey());
        }

        return stream.map(entry -> {
            LocalDate dailyDate = entry.getKey();
            List<Expense> dailyExpenseList = entry.getValue();
            Long dailyTotalCost = dailyTotalCostMap.getOrDefault(dailyDate, 0L);

            List<ExpenseDetailDto> expenseDetails = toMonthlyExpensesDetails(dailyExpenseList);

            return DailyExpensesDto.builder()
                    .date(dailyDate)
                    .dailyTotalCost(dailyTotalCost)
                    .expenseDetailList(expenseDetails)
                    .build();
        }).collect(Collectors.toList());
    }

    public static List<DailyExpensesDto> toMonthlyDailyExpenseDetailsList(Map<LocalDate, List<Expense>> expensesByDate, String order) {
        Stream<Map.Entry<LocalDate, List<Expense>>> stream = expensesByDate.entrySet().stream();

        if ("ASC".equalsIgnoreCase(order)) {
            stream = stream.sorted(Map.Entry.comparingByKey());
        }

        return stream.map(entry -> {
            LocalDate dailyDate = entry.getKey();
            List<Expense> dailyExpenseList = entry.getValue();

            List<ExpenseDetailDto> expenseDetails = toMonthlyExpensesDetails(dailyExpenseList);

            return DailyExpensesDto.builder()
                    .date(dailyDate)
                    .expenseDetailList(expenseDetails)
                    .build();
        }).collect(Collectors.toList());
    }

    public static MonthlyExpenseResponse toMonthlyExpensesResponse(List<DailyExpensesDto> dailyExpensesDtos, Slice<Expense> expenseList) {
        return MonthlyExpenseResponse.builder()
                .hasNext(expenseList.hasNext())
                .dailyExpenseList(dailyExpensesDtos)
                .build();
    }

    private static List<CategoryDetailDto> toCategoryDetails(List<Category> categoryList) {
        return categoryList.stream()
                .filter(category -> category.getStatus() == Status.ACTIVE && category.getIsVisible())
                .sorted(Comparator.comparingLong(Category::getPriority))
                .map(category -> CategoryDetailDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ExpenseDetailDto> toExpensesDetails(List<Expense> expenseList) {

        return expenseList.stream()
                .map(expense -> ExpenseDetailDto.builder()
                        .expenseId(expense.getId())
                        .title(expense.getTitle())
                        .categoryIcon(expense.getCategory().getIcon())
                        .categoryId(expense.getCategory().getId())
                        .cost(expense.getCost())
                        .memo(expense.getMemo())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ExpenseDetailDto> toMonthlyExpensesDetails(List<Expense> expenseList) {

        return expenseList.stream()
                .map(expense -> ExpenseDetailDto.builder()
                        .expenseId(expense.getId())
                        .title(expense.getTitle())
                        .categoryIcon(expense.getCategory().getIcon())
                        .cost(expense.getCost())
                        .build())
                .collect(Collectors.toList());
    }
}
