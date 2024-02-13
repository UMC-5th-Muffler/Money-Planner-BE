package com.umc5th.muffler.message.service.pushAlarm.sender;

import com.umc5th.muffler.message.dto.PushAlarmMessageDTO;
import java.util.List;

public interface PushAlarmSender {
    String send(PushAlarmMessageDTO message);
    List<String> send(List<PushAlarmMessageDTO> messages);
}
