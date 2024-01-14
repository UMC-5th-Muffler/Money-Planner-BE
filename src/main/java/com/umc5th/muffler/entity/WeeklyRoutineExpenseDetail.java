package com.umc5th.muffler.entity;

import java.time.DayOfWeek;
import javax.persistence.*;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
public class WeeklyRoutineExpenseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private RoutineExpense routineExpense;

    public void setRoutineExpense(RoutineExpense routineExpense) {
        this.routineExpense = routineExpense;
    }
}
