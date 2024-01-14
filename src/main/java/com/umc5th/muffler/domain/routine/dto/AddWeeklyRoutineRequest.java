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
    private Integer term;
    @NotNull
    private Long cost;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull
    private String title;
    private String memo;
    @NotNull
    private Long categoryId;
    private Long memberId;
    @NotNull
    private List<Integer> dayOfWeek;
}
