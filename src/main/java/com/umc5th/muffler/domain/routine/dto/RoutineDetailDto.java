package com.umc5th.muffler.domain.routine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoutineDetailDto {
    private Long routineId;
    private String routineTitle;
    private LocalDate endDate;
    private Long categoryId;
    private String categoryIcon;
    private Integer monthlyRepeatDay;
    private RoutineWeeklyDetailDto weeklyDetail;
}
