package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
public class Routine extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoutineType type;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    private String title;

    @Column
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Long cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // Weekly Column
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL)
    private List<WeeklyRepeatDay> weeklyRepeatDays;

    @Column
    private Integer weeklyTerm;

    // Monthly Column
    @Column
    private Integer monthlyRepeatDay;

    public static Routine of(RoutineType type, LocalDate startDate, String title, String memo, Category category, Long cost, Member member) {
        return Routine.builder()
                .type(type)
                .startDate(startDate)
                .title(title)
                .memo(memo)
                .category(category)
                .cost(cost)
                .member(member)
                .build();
    }

    public void setWeeklyColumn(List<WeeklyRepeatDay> weeklyRepeatDays, Integer weeklyTerm) {
        this.weeklyRepeatDays = weeklyRepeatDays;
        this.weeklyTerm = weeklyTerm;
    }

    public void setMonthlyColumn(int monthlyRepeatDay) {
        this.monthlyRepeatDay = monthlyRepeatDay;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
