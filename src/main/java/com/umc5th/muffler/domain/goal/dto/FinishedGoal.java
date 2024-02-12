package com.umc5th.muffler.domain.goal.dto;

import com.umc5th.muffler.message.dto.Alarmable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FinishedGoal implements Alarmable {
    private String goalTitle;
    private String token;
}
