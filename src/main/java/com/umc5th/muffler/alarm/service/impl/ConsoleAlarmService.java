package com.umc5th.muffler.alarm.service.impl;

import com.umc5th.muffler.alarm.dto.Alarmable;
import com.umc5th.muffler.alarm.service.AlarmService;
import java.util.List;
import org.springframework.stereotype.Service;


/*
 * FCM 서비스 구현 이후 삭제 예정
 */
@Service
public class ConsoleAlarmService implements AlarmService {
    @Override
    public void sendAlarm(Alarmable alarmable) {
        System.out.printf("send to : %s\n", alarmable.getToken());
        System.out.printf("title : %s\nbody : %s\n", alarmable.getTitle(), alarmable.getBody());
    }

    @Override
    public <T extends Alarmable> void sendAlarms(List<T> alarmables) {
        alarmables.forEach(alarmable -> {
            System.out.printf("send to : %s\n", alarmable.getToken());
            System.out.printf("title : %s\nbody : %s\n", alarmable.getTitle(), alarmable.getBody());
        });
    }
}
