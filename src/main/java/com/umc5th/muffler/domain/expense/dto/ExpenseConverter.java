package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpenseConverter {

    public static Expense toExpenseEntity(ExpenseCreateRequest request, Member member, Category category) {
        return Expense.builder()
                .title(request.getExpenseTitle())
                .memo(request.getExpenseMemo())
                .date(request.getExpenseDate())
                .cost(request.getExpenseCost())
                .category(category)
                .member(member)
                .build();
    }
    public static Expense toExpenseEntity(ExpenseUpdateRequest request, Member member, Category category) {
        return Expense.builder()
                .id(request.getExpenseId())
                .title(request.getExpenseTitle())
                .memo(request.getExpenseMemo())
                .date(request.getExpenseDate())
                .cost(request.getExpenseCost())
                .category(category)
                .member(member)
                .build();
    }

    public static ExpenseDto toExpenseDto(Expense expense){
        return ExpenseDto.builder()
                .title(expense.getTitle())
                .expenseId(expense.getId())
                .date(expense.getDate())
                .memo(expense.getMemo())
                .cost(expense.getCost())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .categoryIcon(expense.getCategory().getIcon())
                .build();
    }

    public static DailyExpenseResponse toDailyExpensesListWithTotalCost(Slice<Expense> expenseList) {
        List<ExpenseDetailDto> expenseDetails = toExpensesDetails(expenseList.getContent());

        return DailyExpenseResponse.builder()
                .expenseDetailList(expenseDetails)
                .hasNext(expenseList.hasNext())
                .build();
    }

    public static WeeklyExpenseResponse toWeeklyExpensesResponse(List<DailyExpensesDto> dailyExpensesDtos, Slice<Expense> expenseList){
        return WeeklyExpenseResponse.builder()
                .dailyExpenseList(dailyExpensesDtos)
                .hasNext(expenseList.hasNext())
                .build();
    }


    public static List<DailyExpensesDto> toDailyExpensesListWithTotalCost(Map<LocalDate, List<Expense>> expensesByDate, Map<LocalDate, Long> dailyTotalCostMap) {
        List<DailyExpensesDto> dailyExpensesList = expensesByDate.entrySet().stream().map(entry -> {
            LocalDate dailyDate = entry.getKey();
            List<Expense> dailyExpenseList = entry.getValue();
            Long dailyTotalCost = dailyTotalCostMap.getOrDefault(dailyDate, 0L);

            List<ExpenseDetailDto> expenseDetails = toExpensesDetailsWithoutMemo(dailyExpenseList);

            return DailyExpensesDto.builder()
                    .date(dailyDate)
                    .dailyTotalCost(dailyTotalCost)
                    .expenseDetailList(expenseDetails)
                    .build();
        }).collect(Collectors.toList());

        return dailyExpensesList;
    }

    public static List<DailyExpensesDto> toDailyExpensesList(Map<LocalDate, List<Expense>> expensesByDate) {
        return expensesByDate.entrySet().stream().map(entry -> {
            LocalDate dailyDate = entry.getKey();
            List<Expense> dailyExpenseList = entry.getValue();

            List<ExpenseDetailDto> expenseDetails = toExpensesDetailsWithoutMemo(dailyExpenseList);

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

    public static SearchResponse toSearchResponse(List<DailyExpensesDto> expenses, boolean hasNext) {
        return SearchResponse.builder()
                .dailyExpenseList(expenses)
                .hasNext(hasNext)
                .build();
    }

    private static List<ExpenseDetailDto> toExpensesDetails(List<Expense> expenseList) {
        return expenseList.stream()
                .map(expense -> ExpenseDetailDto.builder()
                        .expenseId(expense.getId())
                        .title(expense.getTitle())
                        .categoryIcon(expense.getCategory().getIcon())
                        .cost(expense.getCost())
                        .memo(expense.getMemo())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ExpenseDetailDto> toExpensesDetailsWithoutMemo(List<Expense> expenseList) {
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
