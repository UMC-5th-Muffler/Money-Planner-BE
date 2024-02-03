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
    @Positive
    private Long expenseCost;
    @Positive
    private Long categoryId;
    @NotBlank
    private String expenseTitle;
    private String expenseMemo;
    @NotNull
    private LocalDate expenseDate;
}
