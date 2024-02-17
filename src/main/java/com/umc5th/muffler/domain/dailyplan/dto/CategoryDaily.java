package com.umc5th.muffler.domain.dailyplan.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryDaily implements DailyInfo {
    private LocalDate date;
    private Long dailyCost;
}
