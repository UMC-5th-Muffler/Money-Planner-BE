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
public class RoutineExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Integer day;

    @Column(nullable = true)
    @ColumnDefault("1")
    private Integer term;

    @Column(nullable = false)
    private Long cost;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @Column(nullable = false)
    private String title;

    private String memo;

    @Column(nullable = false)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "routineExpense", cascade = CascadeType.ALL)
    private List<WeeklyRoutineExpenseDetail> detailList = new ArrayList<>();

    /* 연관 관계 메서드 */
    public void addDetail(WeeklyRoutineExpenseDetail detail) {
        detail.setRoutineExpense(this);
        detailList.add(detail);
    }
}
