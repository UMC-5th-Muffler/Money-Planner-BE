package com.umc5th.muffler.alarm.dto;

import com.querydsl.core.annotations.QueryProjection;

public class DailyPlanAlarm implements Alarmable{
    private static final String TITLE = "당일 목표 금액 리마인드";
    private final Long memberName;
    private final String alarmToken;
    private final Long dailyBudget;

    @QueryProjection
    public DailyPlanAlarm(Long memberName, Long dailyBudget, String alarmToken) {
        this.memberName = memberName;
        this.dailyBudget = dailyBudget;
        this.alarmToken = alarmToken;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public String getBody() {
        return "오늘의 소비 목표는 "
                + dailyBudget
                + "원 이에요! 오늘도 "
                + memberName
                + "님의 알뜰한 하루를 응원해요.";
    }

    @Override
    public String getToken() {
        return alarmToken;
    }
}
