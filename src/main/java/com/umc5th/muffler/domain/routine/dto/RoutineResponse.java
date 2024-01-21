package com.umc5th.muffler.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoutineResponse {

    private List<RoutineDetailDto> routineList;
    private boolean hasNext;
}
