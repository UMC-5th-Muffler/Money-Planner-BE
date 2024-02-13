package com.umc5th.muffler.message.service.internal.sender;

import com.umc5th.muffler.message.dto.MessageDTO;
import java.util.List;

public interface InternalAlarmSender {
    String send(MessageDTO message);
    List<String> send(List<MessageDTO> alarms);
}
