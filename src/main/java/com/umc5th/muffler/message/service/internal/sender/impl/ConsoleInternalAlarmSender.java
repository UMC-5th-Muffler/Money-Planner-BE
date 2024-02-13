package com.umc5th.muffler.message.service.internal.sender.impl;

import com.umc5th.muffler.message.dto.MessageDTO;
import com.umc5th.muffler.message.service.internal.sender.InternalAlarmSender;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;


/*
 * FCM 서비스 구현 이후 삭제 예정
 */
@Service
public class ConsoleInternalAlarmSender implements InternalAlarmSender {
    @Override
    public String send(MessageDTO message) {
        System.out.printf("title : %s\nbody : %s\n", message.getTitle(), message.getBody());
        return message.toString();
    }

    @Override
    public int send(List<MessageDTO> messages) {
        List<String> results = new ArrayList<>();

        messages.forEach(message -> {
            System.out.printf("title : %s\nbody : %s\n", message.getTitle(), message.getBody());
            results.add(message.toString());
        });
        return results.size();
    }
}
