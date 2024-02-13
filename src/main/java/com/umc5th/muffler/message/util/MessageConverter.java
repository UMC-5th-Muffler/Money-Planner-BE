package com.umc5th.muffler.message.util;

import com.umc5th.muffler.message.dto.Alarmable;
import com.umc5th.muffler.message.dto.MessageDTO;
import com.umc5th.muffler.message.dto.PushAlarmMessageDTO;

public class MessageConverter {
    public static PushAlarmMessageDTO of(MessageDTO message, Alarmable alarmable) {
        return new PushAlarmMessageDTO(message, alarmable.getToken());
    }
}
