package com.umc5th.muffler.message.service;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import com.umc5th.muffler.message.service.sender.Sender;
import com.umc5th.muffler.message.util.MessageFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final Sender sender;
    public int sendDailyAlarms(List<DailyPlanAlarm> dailyPlanAlarms) {
        return sender.send(dailyPlanAlarms, MessageFormatter::toDailyPlanRemind);
    }

    public int sendTodayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers) {
        return sender.send(notEnrolledMembers, MessageFormatter::toTodayNotEnrollRemind);
    }

    public int sendYesterdayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers) {
        return sender.send(notEnrolledMembers, MessageFormatter::toYesterdayNotEnrollRemind);
    }

    public int sendEndGoals(List<FinishedGoal> finishedGoals) {
        return sender.send(finishedGoals, MessageFormatter::toFinishedGoalRemind);
    }
}
