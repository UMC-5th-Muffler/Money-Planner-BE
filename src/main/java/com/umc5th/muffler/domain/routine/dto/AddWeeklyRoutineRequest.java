package com.umc5th.muffler.domain.routine.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class AddWeeklyRoutineRequest {

    @NotNull
    private Long memberId;
    @NotNull
    private Long cost;
    @NotNull
    private Integer term;
    private LocalDate endDate;
    @NotNull
    private List<Integer> daysOfWeek;
}
