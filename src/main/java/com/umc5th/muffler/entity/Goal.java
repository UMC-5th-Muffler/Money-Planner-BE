package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
public class Goal extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String title;

    @Column(length = 1024)
    private String memo;

    @Column
    private String icon;

    @Column(nullable = false)
    private Long totalBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL)
    private List<CategoryGoal> categoryGoals;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL)
    private List<DailyPlan> dailyPlans;

    public static Goal of(LocalDate startDate, LocalDate endDate, String title, String memo, String icon, Long totalBudget, Member member) {
        return Goal.builder()
                .startDate(startDate)
                .endDate(endDate)
                .title(title)
                .memo(memo)
                .icon(icon)
                .totalBudget(totalBudget)
                .member(member)
                .build();
    }

    public void setDailyPlans(List<DailyPlan> dailyPlans) {
        this.dailyPlans = dailyPlans;
        dailyPlans.forEach(dailyPlan -> dailyPlan.setGoal(this));
    }

    public void setCategoryGoals(List<CategoryGoal> categoryGoals) {
        this.categoryGoals = categoryGoals;
        categoryGoals.forEach(categoryGoal -> categoryGoal.setGoal(this));
    }

    public Boolean isPossibleToAlarm(Long sum, Long addition) {
        return sum <= totalBudget && totalBudget < sum + addition;
    }
}
