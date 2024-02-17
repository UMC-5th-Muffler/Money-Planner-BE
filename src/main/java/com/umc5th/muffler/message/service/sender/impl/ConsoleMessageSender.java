package com.umc5th.muffler.message.service.sender.impl;

import com.umc5th.muffler.message.dto.Alarmable;
import com.umc5th.muffler.message.dto.MessageDTO;
import com.umc5th.muffler.message.service.sender.Sender;
import java.util.List;
import java.util.function.Function;


/*
 * FCM 서비스 구현 이후 삭제 예정
 */
public class ConsoleMessageSender implements Sender {
    @Override
    public <T extends Alarmable> int send(List<T> data, Function<T, MessageDTO> formatter) {
        data.stream().map(formatter)
                .forEach(message -> System.out.printf("title : %s\n, body : %s\n", message.getTitle(), message.getBody()));
        return data.size();
    }
}
