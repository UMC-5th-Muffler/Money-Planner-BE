package com.umc5th.muffler.domain.dailyplan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umc5th.muffler.domain.category.dto.OutlineCategoryDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WholeCalendar {
    private ActiveGoalResponse activeGoalResponse;
    private List<InactiveGoalInfo> inactiveGoalsResponse;
    private List<OutlineCategoryDTO> categoryFilters;
}
