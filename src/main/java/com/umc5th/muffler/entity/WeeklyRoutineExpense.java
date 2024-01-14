package com.umc5th.muffler.entity;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@DynamicInsert
public class WeeklyRoutineExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer term;

    @Column(nullable = false)
    private Long cost;

    @Column(nullable = true)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "weeklyRoutineExpense", cascade = CascadeType.ALL)
    private List<WeeklyRoutineExpenseDetail> detailList = new ArrayList<>();

    public void addDetail(WeeklyRoutineExpenseDetail detail) {
        detailList.add(detail);
        detail.setWeeklyRoutineExpense(this);
    }
}
