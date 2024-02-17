package com.umc5th.muffler.domain.routine.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RoutineWeeklyDetailDto {
    private Integer weeklyTerm;
    private List<Integer> weeklyRepeatDays;
}
