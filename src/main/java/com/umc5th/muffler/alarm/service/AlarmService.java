package com.umc5th.muffler.alarm.service;

import com.umc5th.muffler.alarm.dto.Alarmable;
import java.util.List;

public interface AlarmService {
    void sendAlarm(Alarmable alarmable);
    <T extends Alarmable> void sendAlarms(List<T> alarmables);
}
