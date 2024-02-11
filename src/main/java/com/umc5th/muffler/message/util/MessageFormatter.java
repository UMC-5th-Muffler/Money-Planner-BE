package com.umc5th.muffler.message.util;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.message.dto.Message;
import java.text.DecimalFormat;

public class MessageFormatter {
    private static final DecimalFormat expenseFormatter = new DecimalFormat("###,###");

    public static Message toDailyPlanRemind(DailyPlanAlarm dailyPlanAlarm) {
        String title = "당일 목표 리마인드";
        String body = String.format("오늘의 소비 목표는 %s원 이에요! 오늘도 %s님의 알뜰한 하루를 응원해요",
                expenseFormatter.format(dailyPlanAlarm.getDailyBudget()), dailyPlanAlarm.getMemberName());
        return new Message(title, body);
    }

    public static Message toTodayNotEnrollRemind() {
        String title = "소비 내역 기록";
        String body = "오늘 등록된 소비내역이 없어요. 0원 소비를 하셨더라도 체크해주세요!";
        return new Message(title, body);
    }
    
    public static Message toYesterdayNotEnrollRemind() {
        String title = "소비 내역 기록";
        String body = "어제 등록된 소비내역이 없어요. 0원 소비를 하셨더라도 체크해주세요!";
        return new Message(title, body);
    }
}
