package com.umc5th.muffler.message.service.pushAlarm;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import com.umc5th.muffler.message.dto.Alarmable;
import com.umc5th.muffler.message.dto.MessageDTO;
import com.umc5th.muffler.message.dto.PushAlarmMessageDTO;
import com.umc5th.muffler.message.service.pushAlarm.sender.PushAlarmSender;
import com.umc5th.muffler.message.service.pushAlarm.sender.fcm.FCMSender;
import com.umc5th.muffler.message.service.AlarmService;
import com.umc5th.muffler.message.util.MessageConverter;
import com.umc5th.muffler.message.util.MessageFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PushAlarmService implements AlarmService {
    private final PushAlarmSender sender;

    public PushAlarmService() {
        sender = new FCMSender();
    }

    public List<String> sendDailyAlarms(List<DailyPlanAlarm> dailyPlanAlarms) {
        return sendPushAlarms(dailyPlanAlarms, MessageFormatter::toDailyPlanRemind);
    }

    @Override
    public List<String> sendTodayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers) {
        return sendPushAlarms(notEnrolledMembers, MessageFormatter::toTodayNotEnrollRemind);
    }

    @Override
    public List<String> sendYesterdayNotEnrolled(List<NotEnrolledMember> notEnrolledMembers) {
        return sendPushAlarms(notEnrolledMembers, MessageFormatter::toYesterdayNotEnrollRemind);
    }

    @Override
    public List<String> sendEndGoals(List<FinishedGoal> finishedGoals) {
        return sendPushAlarms(finishedGoals, MessageFormatter::toFinishedGoalRemind);
    }

    public <T extends Alarmable> List<String> sendPushAlarms(List<T> data, Function<T, MessageDTO> formatter) {
        List<PushAlarmMessageDTO> pushAlarms = data.stream().map(datum -> {
            MessageDTO message = formatter.apply(datum);
            return MessageConverter.of(message, datum);
        }).collect(Collectors.toList());
        return sender.send(pushAlarms);
    }
}
