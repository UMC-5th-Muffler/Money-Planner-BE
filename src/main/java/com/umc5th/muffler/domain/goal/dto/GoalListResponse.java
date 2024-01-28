package com.umc5th.muffler.domain.goal.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GoalListResponse {

    private List<GoalListInfo> goalList;
}
