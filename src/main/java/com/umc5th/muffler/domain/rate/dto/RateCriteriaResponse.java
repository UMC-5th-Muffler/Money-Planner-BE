package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.constant.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RateCriteriaResponse {

    private Long rateId;
    private Level totalLevel;
    private String memo;
    private Long dailyPlanBudget;
    private Long dailyTotalCost;
    private Boolean isZeroDay;
}
