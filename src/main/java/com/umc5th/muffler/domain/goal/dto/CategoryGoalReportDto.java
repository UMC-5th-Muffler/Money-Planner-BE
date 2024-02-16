package com.umc5th.muffler.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryGoalReportDto {
    private String categoryName;
    private String categoryIcon;
    private Long categoryBudget;
    @Builder.Default
    private Long totalCost = 0L;
    @Builder.Default
    private Long avgCost = 0L;
    @Builder.Default
    private Long maxCost = 0L;
    @Builder.Default
    private int expenseCount = 0;

    public void addExpense(long cost) {
        this.totalCost += cost;
        this.expenseCount++;
        this.maxCost = Math.max(this.maxCost, cost);
    }

    public void calculateAvgCost() {
        if (this.expenseCount > 0) {
            this.avgCost = this.totalCost / this.expenseCount;
        }
    }
}
