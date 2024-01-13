package com.umc5th.muffler.domain.routine.dto;

import com.sun.istack.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class WeeklyRoutineRequest {

    @NotNull
    private Long memberId;
    @NotNull
    private Integer cost;
    @NotNull
    private Integer term;
    private LocalDate endDate;
    @NotNull
    private List<Integer> daysOfWeek;
}
