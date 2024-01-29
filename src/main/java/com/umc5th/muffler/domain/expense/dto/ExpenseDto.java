package com.umc5th.muffler.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
    private Long expenseId;
    private LocalDate date;
    private String title;
    private String memo;
    private Long cost;
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
}
