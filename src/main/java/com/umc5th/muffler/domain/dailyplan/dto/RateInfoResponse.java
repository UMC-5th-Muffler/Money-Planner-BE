package com.umc5th.muffler.domain.dailyplan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umc5th.muffler.entity.constant.Level;
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
    private Level rate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String memo;
    private Long dailyPlanBudget;
    private Long dailyTotalCost;
    private Boolean isZeroDay;
}
