package com.umc5th.muffler.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDetailDto { // 소비내역 상세

    private Long expenseId;
    private String title;
    private Long cost;
    private String memo;
    private Long categoryId;
    private String categoryIcon;
}
