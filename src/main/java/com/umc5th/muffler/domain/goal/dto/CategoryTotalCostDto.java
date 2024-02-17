package com.umc5th.muffler.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTotalCostDto {
    private String categoryName;
    private Long totalCost;
}
