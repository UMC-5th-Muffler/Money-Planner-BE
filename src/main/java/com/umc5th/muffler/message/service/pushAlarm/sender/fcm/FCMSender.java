package com.umc5th.muffler.message.service.pushAlarm.sender.fcm;

import com.umc5th.muffler.message.dto.PushAlarmMessageDTO;
import com.umc5th.muffler.message.service.pushAlarm.sender.PushAlarmSender;
import java.util.List;

public class FCMSender implements PushAlarmSender {

    @Override
    public String send(PushAlarmMessageDTO message) {
        return null;
    }

    @Override
    public List<String> send(List<PushAlarmMessageDTO> messages) {
        return null;
    }
}
