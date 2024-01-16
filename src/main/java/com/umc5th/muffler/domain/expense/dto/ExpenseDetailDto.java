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

    private Long expenseId; // 소비내역 id
    private String title; // 제목
    private Long cost; // 금액
    private Long categoryId; // 카테고리 id
    private String categoryIcon; // 카테고리 아이콘
}
