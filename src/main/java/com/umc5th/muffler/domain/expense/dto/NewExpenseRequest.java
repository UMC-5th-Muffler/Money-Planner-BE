package com.umc5th.muffler.domain.expense.dto;

import com.sun.istack.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewExpenseRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String expenseName;
    @Positive
    private Long expenseCost;
    private String expenseDescription;
    @NotBlank
    private String categoryName;
}
