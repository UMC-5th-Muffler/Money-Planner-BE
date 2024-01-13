package com.umc5th.muffler.domain.expense.converter;

import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.dto.ExpenseDetail;
import com.umc5th.muffler.domain.expense.dto.WeeklyExpenseDetailsResponse;
import com.umc5th.muffler.entity.Expense;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseConverter {

    public static DailyExpenseDetailsResponse toDailyExpenseDetail(Slice<Expense> expenseList, LocalDate date){

        // Expense(entity) -> ExpenseDetail(dto)
        List<ExpenseDetail> expenseDetails = expenseList
                .stream()
                .map(expense -> ExpenseDetail.builder()
                        .id(expense.getId())
                        .title(expense.getTitle())
                        .categoryIcon(expense.getCategory().getIcon())
                        .cost(expense.getCost())
                        .build())
                .collect(Collectors.toList());

        // 일일 소비 총합 계산
        long totalCostSum = expenseList.stream().mapToLong(Expense::getCost).sum();

        return  DailyExpenseDetailsResponse.builder()
                .dailyTotalCost(totalCostSum)
                .date(date)
                .expenseDetailList(expenseDetails)
                .hasNext(expenseList.hasNext())
                .build();
    }

}
