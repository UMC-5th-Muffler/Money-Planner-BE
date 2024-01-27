package com.umc5th.muffler.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class NewExpenseRequest {
    @NotBlank
    private String expenseTitle;
    @Positive
    private Long expenseCost;
    @NotNull
    private LocalDate expenseDate;
    private String expenseMemo;
    @NotBlank
    private String categoryName;
}
