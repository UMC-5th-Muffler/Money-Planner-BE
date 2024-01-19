package com.umc5th.muffler.domain.home.dto;

import com.umc5th.muffler.entity.constant.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyCategoryInfoDto {

    private Long dailyTotalCost;
    private Level dailyRate;
}