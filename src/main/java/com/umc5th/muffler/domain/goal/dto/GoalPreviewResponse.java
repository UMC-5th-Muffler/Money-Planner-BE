package com.umc5th.muffler.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
public class GoalPreviewResponse {

    private GoalPreviewInfo progressGoal;
    private List<GoalPreviewInfo> endedGoal;
    private List<GoalPreviewInfo> futureGoal;
}
