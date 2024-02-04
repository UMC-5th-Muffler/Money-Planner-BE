package com.umc5th.muffler.domain.dailyplan.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonDeserialize(as = InactiveDaily.class)
@Getter
@AllArgsConstructor
public class InactiveDaily implements DailyInfo {
    private LocalDate date;
    private Rate dailyRate;
}
