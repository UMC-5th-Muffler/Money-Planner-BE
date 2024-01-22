package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.constant.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRateResponse {
    private Long categoryGoalId;
    private String categoryName;
    private Long categoryRateId;
    private Level level;
}
