package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.domain.routine.dto.RoutineRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ExpenseCreateRequest {
    @Positive
    private Long expenseCost;
    @NotNull
    private Long categoryId;
    @NotBlank
    private String expenseTitle;
    private String expenseMemo;
    @NotNull
    private LocalDate expenseDate;

    @NotNull
    private boolean isRoutine;
    private RoutineRequest routineRequest;
}
