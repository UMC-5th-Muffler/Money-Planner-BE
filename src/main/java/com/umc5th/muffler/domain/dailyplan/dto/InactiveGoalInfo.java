package com.umc5th.muffler.domain.dailyplan.dto;

import com.umc5th.muffler.entity.constant.Rate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class InactiveGoalInfo {
    private LocalDate otherStartDate;
    private LocalDate otherEndDate;
    private List<Rate> totalRateList;
}
