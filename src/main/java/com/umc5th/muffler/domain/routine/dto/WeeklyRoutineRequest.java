package com.umc5th.muffler.domain.routine.dto;

import com.sun.istack.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class WeeklyRoutineRequest {


    private Long memberId;
    @NotNull
    private Integer cost;
    @NotNull
    private Integer term;
    @NotNull
    private List<Integer> daysOfWeek;
}
