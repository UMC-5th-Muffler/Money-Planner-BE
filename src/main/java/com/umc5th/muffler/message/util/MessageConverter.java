package com.umc5th.muffler.message.util;

import com.umc5th.muffler.message.dto.Alarmable;
import com.umc5th.muffler.message.dto.Message;
import com.umc5th.muffler.message.dto.PushAlarmMessage;

public class MessageConverter {
    public static PushAlarmMessage of(Message message, Alarmable alarmable) {
        return new PushAlarmMessage(message, alarmable.getToken());
    }
}
