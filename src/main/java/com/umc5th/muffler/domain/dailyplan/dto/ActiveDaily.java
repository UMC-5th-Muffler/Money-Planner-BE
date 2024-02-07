package com.umc5th.muffler.domain.dailyplan.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@JsonDeserialize(as = ActiveDaily.class)
@Builder
@Getter
public class ActiveDaily implements DailyInfo {
    private LocalDate date;
    private Long dailyBudget;
    private Long dailyTotalCost;
    private Rate dailyRate;
    private Boolean isZeroDay;
}
