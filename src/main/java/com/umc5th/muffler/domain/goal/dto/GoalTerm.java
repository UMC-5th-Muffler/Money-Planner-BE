package com.umc5th.muffler.domain.goal.dto;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class GoalTerm {
    private LocalDate startDate;
    private LocalDate endDate;

    @QueryProjection
    public GoalTerm(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
