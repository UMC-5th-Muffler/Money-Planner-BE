package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @ColumnDefault("false")
    private Boolean isZeroDay;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long totalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long totalCost;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isZeroDay;

    public static DailyPlan of(LocalDate date, Long budget) {
        return DailyPlan.builder()
                .date(date)
                .budget(budget)
                .build();
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
