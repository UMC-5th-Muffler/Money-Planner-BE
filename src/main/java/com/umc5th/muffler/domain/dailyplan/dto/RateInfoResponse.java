package com.umc5th.muffler.domain.dailyplan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umc5th.muffler.entity.constant.Rate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RateInfoResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Rate rate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rateMemo;
    private Long dailyPlanBudget;
    private Long dailyTotalCost;
    private Boolean isZeroDay;
}
