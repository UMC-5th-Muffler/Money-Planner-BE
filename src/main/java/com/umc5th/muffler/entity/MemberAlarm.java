package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
public class MemberAlarm extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isDailyPlanRemindAgree;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isTodayEnrollRemindAgree;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isYesterdayEnrollRemindAgree;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isGoalEndReportRemindAgree;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Member member;
    public void setMember(Member member) {
        this.member = member;
    }
    public void enrollToken(String token) {
        this.token = token;
    }
}
