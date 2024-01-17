package com.umc5th.muffler.domain.routine.dto;

import com.umc5th.muffler.entity.constant.RoutineType;
import com.umc5th.muffler.global.validation.ValidDayOfWeekList;
import com.umc5th.muffler.global.validation.ValidEnum;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineRequest {
    @NotNull
    @ValidEnum(enumClass = RoutineType.class)
    private RoutineType type;
    private LocalDate endDate;

    @ValidDayOfWeekList
    private List<DayOfWeek> weeklyRepeatDays;
    @Pattern(regexp = "^([1-3])?$")
    private String weeklyTerm;

    @Pattern(regexp = "^([1-9]|[12][0-9]|3[01])?$")
    private String monthlyRepeatDay;
}
