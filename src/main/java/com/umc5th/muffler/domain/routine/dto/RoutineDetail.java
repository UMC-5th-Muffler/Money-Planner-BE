package com.umc5th.muffler.domain.routine.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoutineDetail {
    private String routineMemo;
    private String categoryName;
}
