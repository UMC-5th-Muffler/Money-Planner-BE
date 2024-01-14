package com.umc5th.muffler.domain.goal.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalPreviousResponse {
    private List<GoalTerm> terms;
}
