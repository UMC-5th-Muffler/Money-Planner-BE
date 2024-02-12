package com.umc5th.muffler.message.service.internal;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import com.umc5th.muffler.message.dto.Message;
import com.umc5th.muffler.message.service.internal.sender.InternalAlarmSender;
import com.umc5th.muffler.message.service.internal.sender.impl.ConsoleInternalAlarmSender;
import com.umc5th.muffler.message.service.AlarmService;
import com.umc5th.muffler.message.util.MessageFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class InternalAlarmService implements AlarmService {
    private final InternalAlarmSender sender;

    public InternalAlarmService() {
        sender = new ConsoleInternalAlarmSender();
    }

    @Override
    public List<String> sendDailyAlarms(List<DailyPlanAlarm> dailyPlanAlarms) {
        List<Message> messages = dailyPlanAlarms.stream()
                .map(MessageFormatter::toDailyPlanRemind)
                .collect(Collectors.toList());
        return sender.send(messages);
    }

    @Override
    public List<String> sendTodayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers) {
        List<Message> messages = notEnrolledMembers.stream()
                .map((member) -> MessageFormatter.toTodayNotEnrollRemind())
                .collect(Collectors.toList());
        return sender.send(messages);
    }

    @Override
    public List<String> sendYesterdayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers) {
        List<Message> messages = notEnrolledMembers.stream()
                .map((member) -> MessageFormatter.toYesterdayNotEnrollRemind())
                .collect(Collectors.toList());
        return sender.send(messages);
    }

    @Override
    public List<String> sendEndGoals(List<FinishedGoal> finishedGoals) {
        List<Message> messages = finishedGoals.stream()
                .map(MessageFormatter::toFinishedGoalRemind)
                .collect(Collectors.toList());
        return sender.send(messages);
    }
}
