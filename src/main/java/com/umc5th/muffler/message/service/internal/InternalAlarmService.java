package com.umc5th.muffler.message.service.internal;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import com.umc5th.muffler.message.dto.MessageDTO;
import com.umc5th.muffler.message.service.internal.sender.InternalAlarmSender;
import com.umc5th.muffler.message.service.internal.sender.impl.ConsoleInternalAlarmSender;
import com.umc5th.muffler.message.service.AlarmService;
import com.umc5th.muffler.message.util.MessageFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class InternalAlarmService implements AlarmService {
    private final InternalAlarmSender sender;

    public InternalAlarmService() {
        sender = new ConsoleInternalAlarmSender();
    }

    @Override
    public int sendDailyAlarms(List<DailyPlanAlarm> dailyPlanAlarms) {
        return send(dailyPlanAlarms, MessageFormatter::toDailyPlanRemind);
    }

    @Override
    public int sendTodayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers) {
        return send(notEnrolledMembers, MessageFormatter::toTodayNotEnrollRemind);
    }

    @Override
    public int sendYesterdayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers) {
        return send(notEnrolledMembers, MessageFormatter::toYesterdayNotEnrollRemind);
    }

    @Override
    public int sendEndGoals(List<FinishedGoal> finishedGoals) {
        return send(finishedGoals, MessageFormatter::toFinishedGoalRemind);
    }

    private <T> int send(List<T> data, Function<T, MessageDTO> formatter) {
        List<MessageDTO> messages = data.stream()
                .map(formatter)
                .collect(Collectors.toList());
        return sender.send(messages);
    }
}
