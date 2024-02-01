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
    public static Expense toExpenseEntity(UpdateExpenseRequest request, Member member, Category category) {
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

    public static DailyExpenseResponse toDailyExpensesList(LocalDate date, Slice<Expense> expenseList, DailyPlan dailyPlan) {
        List<ExpenseDetailDto> expenseDetails = toExpensesDetailsInDaily(expenseList.getContent());

        return DailyExpenseResponse.builder()
                .dailyTotalCost(dailyPlan.getTotalCost())
                .date(date)
                .isZeroDay(dailyPlan.getIsZeroDay())
                .rate(dailyPlan.getRate())
                .rateMemo(dailyPlan.getRateMemo())
                .expenseDetailList(expenseDetails)
                .hasNext(expenseList.hasNext())
                .build();
    }

    public static WeeklyExpenseResponse toWeeklyExpensesResponse(List<DailyExpensesDto> dailyExpensesDtos, Slice<Expense> expenseList,
                                                                 List<Category> categoryList){
        List<CategoryDetailDto> categoryDetails = toCategoryDetails(categoryList);

        return WeeklyExpenseResponse.builder()
                .categoryList(categoryDetails)
                .dailyExpenseList(dailyExpensesDtos)
                .hasNext(expenseList.hasNext())
                .build();
    }


    public static List<DailyExpensesDto> toDailyExpensesList(Map<LocalDate, List<Expense>> expensesByDate, Map<LocalDate, Long> dailyTotalCostMap) {
        return expensesByDate.entrySet().stream().map(entry -> {
            LocalDate dailyDate = entry.getKey();
            List<Expense> dailyExpenseList = entry.getValue();
            Long dailyTotalCost = dailyTotalCostMap.getOrDefault(dailyDate, 0L);

            List<ExpenseDetailDto> expenseDetails = toExpensesDetailsInWeekly(dailyExpenseList);

            return DailyExpensesDto.builder()
                    .date(dailyDate)
                    .dailyTotalCost(dailyTotalCost)
                    .expenseDetailList(expenseDetails)
                    .build();
        }).collect(Collectors.toList());
    }

    public static List<DailyExpensesDto> toDailyExpensesListWithOrderAndTotalCost(Map<LocalDate, List<Expense>> expensesByDate, Map<LocalDate, Long> dailyTotalCostMap, String order) {
        Stream<Map.Entry<LocalDate, List<Expense>>> stream = expensesByDate.entrySet().stream();

        if ("ASC".equalsIgnoreCase(order)) {
            stream = stream.sorted(Map.Entry.comparingByKey());
        }

        return stream.map(entry -> {
            LocalDate dailyDate = entry.getKey();
            List<Expense> dailyExpenseList = entry.getValue();
            Long dailyTotalCost = dailyTotalCostMap.getOrDefault(dailyDate, 0L);

            List<ExpenseDetailDto> expenseDetails = toExpensesDetailsInMonthly(dailyExpenseList);

            return DailyExpensesDto.builder()
                    .date(dailyDate)
                    .dailyTotalCost(dailyTotalCost)
                    .expenseDetailList(expenseDetails)
                    .build();
        }).collect(Collectors.toList());
    }

    public static List<DailyExpensesDto> toDailyExpensesListWithOrderAndTotalCost(Map<LocalDate, List<Expense>> expensesByDate, String order) {
        Stream<Map.Entry<LocalDate, List<Expense>>> stream = expensesByDate.entrySet().stream();

        if ("ASC".equalsIgnoreCase(order)) {
            stream = stream.sorted(Map.Entry.comparingByKey());
        }

        return stream.map(entry -> {
            LocalDate dailyDate = entry.getKey();
            List<Expense> dailyExpenseList = entry.getValue();

            List<ExpenseDetailDto> expenseDetails = toExpensesDetailsInMonthly(dailyExpenseList);

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

    private static List<ExpenseDetailDto> toExpensesDetailsInDaily(List<Expense> expenseList) {

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

    private static List<ExpenseDetailDto> toExpensesDetailsInWeekly(List<Expense> expenseList) {

        return expenseList.stream()
                .map(expense -> ExpenseDetailDto.builder()
                        .expenseId(expense.getId())
                        .title(expense.getTitle())
                        .categoryId(expense.getCategory().getId())
                        .categoryIcon(expense.getCategory().getIcon())
                        .cost(expense.getCost())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ExpenseDetailDto> toExpensesDetailsInMonthly(List<Expense> expenseList) {

        return expenseList.stream()
                .map(expense -> ExpenseDetailDto.builder()
                        .expenseId(expense.getId())
                        .title(expense.getTitle())
                        .categoryIcon(expense.getCategory().getIcon())
                        .cost(expense.getCost())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<DailyExpensesDto> toSearch(Map<LocalDate, List<Expense>> expensesByDate) {

        return expensesByDate.entrySet().stream().map(entry -> {
            LocalDate date = entry.getKey();
            List<Expense> expenseList = entry.getValue();

            List<ExpenseDetailDto> expenseDetailDtos = expenseList.stream()
                    .map(expense -> ExpenseDetailDto.builder()
                            .expenseId(expense.getId())
                            .title(expense.getTitle())
                            .cost(expense.getCost())
                            .categoryIcon(expense.getCategory().getIcon())
                            .build())
                    .collect(Collectors.toList());

            return DailyExpensesDto.builder()
                    .date(date)
                    .expenseDetailList(expenseDetailDtos)
                    .build();
        }).collect(Collectors.toList());
    }

    public static SearchResponse toSearchResponse(List<DailyExpensesDto> expenses, boolean hasNext) {

        return SearchResponse.builder()
                .dailyExpenseList(expenses)
                .hasNext(hasNext)
                .build();
    }

}
