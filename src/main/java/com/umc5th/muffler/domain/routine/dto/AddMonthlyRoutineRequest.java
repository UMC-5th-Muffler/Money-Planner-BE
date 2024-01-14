package com.umc5th.muffler.domain.routine.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AddMonthlyRoutineRequest {

    @NotNull
    private Long memberId;
    @NotNull
    private Long cost;
    @NotNull
    private Integer day;
    private LocalDate endDate;
}
