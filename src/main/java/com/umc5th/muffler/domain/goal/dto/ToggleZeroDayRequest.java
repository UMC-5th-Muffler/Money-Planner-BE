package com.umc5th.muffler.domain.goal.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToggleZeroDayRequest {
    @NotNull
    private LocalDate dailyPlanDate;
}
