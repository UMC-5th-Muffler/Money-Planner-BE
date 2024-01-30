package com.umc5th.muffler.domain.expense.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateExpenseRequest {
    @NotNull
    @Positive
    private Long expenseId;
    @NotBlank
    private String expenseTitle;
    @Positive
    private Long expenseCost;
    @NotNull
    private LocalDate expenseDate;
    private String expenseMemo;
    @Positive
    private Long categoryId;
}
