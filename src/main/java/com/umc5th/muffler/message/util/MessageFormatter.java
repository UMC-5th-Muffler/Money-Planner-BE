package com.umc5th.muffler.message.util;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import com.umc5th.muffler.message.dto.MessageDTO;
import java.text.DecimalFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageFormatter {
    @Value("alarm.image-url.daily-plan-remind")
    private static String DAILY_PLAN_REMIND_IMAGE;
    @Value("alarm.image-url.expense-enroll-remind")
    private static String EXPENSE_ENROLL_IMAGE;
    @Value("alarm.image-url.goal-end-remind")
    private static String GOAL_END_IMAGE;

    private static final DecimalFormat expenseFormatter = new DecimalFormat("###,###");

    public static MessageDTO toDailyPlanRemind(DailyPlanAlarm dailyPlanAlarm) {
        String title = "당일 목표 리마인드";
        String body = String.format("오늘의 소비 목표는 %s원 이에요! 오늘도 %s님의 알뜰한 하루를 응원해요",
                expenseFormatter.format(dailyPlanAlarm.getDailyBudget()), dailyPlanAlarm.getMemberName());
        return new MessageDTO(title, body, DAILY_PLAN_REMIND_IMAGE);
    }

    public static MessageDTO toTodayNotEnrollRemind(NotEnrolledMember notEnrolledMembers) {
        String title = "소비 내역 기록";
        String body = "오늘 등록된 소비내역이 없어요. 0원 소비를 하셨더라도 체크해주세요!";
        return new MessageDTO(title, body, EXPENSE_ENROLL_IMAGE);
    }
    
    public static MessageDTO toYesterdayNotEnrollRemind(NotEnrolledMember notEnrolledMember) {
        String title = "소비 내역 기록";
        String body = "어제 등록된 소비내역이 없어요. 0원 소비를 하셨더라도 체크해주세요!";
        return new MessageDTO(title, body, EXPENSE_ENROLL_IMAGE);
    }

    public static MessageDTO toFinishedGoalRemind(FinishedGoal finishedGoal) {
        String title = "목표 종료";
        String body = String.format("%s'%s' 목표가 종료되었어요. 목표 기간 동안의 소비를 분석해드렸어요!",
                finishedGoal.getGoalIcon(), finishedGoal.getGoalTitle());
        return new MessageDTO(title, body, GOAL_END_IMAGE);
    }
}
