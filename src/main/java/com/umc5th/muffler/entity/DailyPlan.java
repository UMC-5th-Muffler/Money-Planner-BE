package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
public class DailyPlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long budget;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isZeroDay = false;

    @Column(nullable = false)
    @Builder.Default
    private Long totalCost = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_id")
    private Rate rate;

    public static DailyPlan of(LocalDate date, Long budget) {
        return DailyPlan.builder()
                .date(date)
                .budget(budget)
                .build();
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public void setRate(Rate rate){
        this.rate = rate;
    }
    public void addExpenseDifference(Long difference) {
        this.totalCost += difference;
    }
}
