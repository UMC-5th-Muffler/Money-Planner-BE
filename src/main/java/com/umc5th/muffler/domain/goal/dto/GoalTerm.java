package com.umc5th.muffler.domain.goal.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalTerm {
    private LocalDate startDate;
    private LocalDate endDate;
}
