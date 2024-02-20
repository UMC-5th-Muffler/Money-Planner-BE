package com.umc5th.muffler.entity;

import static com.umc5th.muffler.entity.constant.MonthlyRepeatType.*;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import com.umc5th.muffler.entity.constant.MonthlyRepeatType;
import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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
import org.hibernate.annotations.BatchSize;

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
    @Builder.Default
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<WeeklyRepeatDay> weeklyRepeatDays = new ArrayList<>();

    @Column
    private Integer weeklyTerm;

    // Monthly Column
    @Column
    @Enumerated(EnumType.STRING)
    private MonthlyRepeatType monthlyRepeatType;

    @Column
    private Integer specificDay; // SPECIFIC_DAY_OF_MONTH에 해당하는 일

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

    public String monthlyRepeatAsString() {
        if (this.monthlyRepeatType == SPECIFIC_DAY_OF_MONTH) {
            return String.format("매월 %d일", specificDay);
        }
        return this.monthlyRepeatType.toString();
    }

    public int getMonthlyRepeatDay(LocalDate date) {
        if (this.monthlyRepeatType == FIRST_DAY_OF_MONTH) {
            return 1;
        }
        if (this.monthlyRepeatType == LAST_DAY_OF_MONTH) {
            return YearMonth.from(date).lengthOfMonth();
        }
        return this.specificDay;
    }

    public void setWeeklyColumn(List<WeeklyRepeatDay> weeklyRepeatDays, Integer weeklyTerm) {
        this.weeklyRepeatDays = weeklyRepeatDays;
        this.weeklyTerm = weeklyTerm;
    }

    public void setMonthlyColumn(MonthlyRepeatType monthlyRepeatType) {
        this.monthlyRepeatType = monthlyRepeatType;
        if (monthlyRepeatType == SPECIFIC_DAY_OF_MONTH) {
            this.specificDay = startDate.getDayOfMonth();
        }
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void addRepeatDay(WeeklyRepeatDay weeklyRepeatDay) {
        this.weeklyRepeatDays.add(weeklyRepeatDay);
    }
}
