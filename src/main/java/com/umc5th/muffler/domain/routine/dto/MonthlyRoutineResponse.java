package com.umc5th.muffler.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRoutineResponse {
    Long monthlyRoutineId;
}
