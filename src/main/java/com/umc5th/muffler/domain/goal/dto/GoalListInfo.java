package com.umc5th.muffler.domain.goal.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GoalListInfo {
    private Long goalId;
    private String goalTitle;
    private String icon;
}
