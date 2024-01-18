package com.umc5th.muffler.domain.home.dto;

import com.umc5th.muffler.entity.constant.Level;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DailyCategoryInfoDto {

    private Long dailyTotalCost;
    private Level dailyRate;
}