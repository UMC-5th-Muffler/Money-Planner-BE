package com.umc5th.muffler.message.service.internal.sender;

import com.umc5th.muffler.message.dto.Message;
import java.util.List;

public interface InternalAlarmSender {
    String send(Message message);
    List<String> send(List<Message> alarms);
}
