package com.umc5th.muffler.message.service.internal;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
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
}
