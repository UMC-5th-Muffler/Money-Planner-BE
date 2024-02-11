package com.umc5th.muffler.domain.member.dto;


import com.umc5th.muffler.message.dto.Alarmable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TodayNotEnrolledMember implements Alarmable {
    private String alarmToken;

    @Override
    public String getToken() {
        return alarmToken;
    }
}
