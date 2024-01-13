package com.umc5th.muffler.domain.routine.dto;

import com.sun.istack.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MonthlyRoutineRequest {

    @NotNull
    private Long memberId;
    @NotNull
    private Long cost;
    @NotNull
    private Integer day;
    private LocalDate endDate;
}
