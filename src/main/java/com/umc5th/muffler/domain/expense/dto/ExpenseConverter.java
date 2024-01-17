package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.domain.expense.dto.CategoryDetailDto;
import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.dto.ExpenseDetailDto;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
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

    public static DailyExpenseDetailsResponse toDailyExpenseDetail(Slice<Expense> expenseList, List<Category> categoryList, LocalDate date){

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

        // 일일 소비 총합 계산
        long totalCostSum = expenseList.stream().mapToLong(Expense::getCost).sum();

        return  DailyExpenseDetailsResponse.builder()
                .dailyTotalCost(totalCostSum)
                .date(date)
                .expenseDetailDtoList(expenseDetails)
                .categoryList(categoryDetails)
                .hasNext(expenseList.hasNext())
                .build();
    }
}
