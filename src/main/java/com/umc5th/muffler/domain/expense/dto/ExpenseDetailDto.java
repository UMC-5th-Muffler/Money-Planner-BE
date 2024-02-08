package com.umc5th.muffler.domain.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDetailDto {

    private Long expenseId;
    private String title;
    private Long cost;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String memo;
    private String categoryIcon;
}
