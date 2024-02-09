package com.umc5th.muffler.alarm.service;

import java.util.List;

public interface AlarmService {
    void sendAlarm(Alarmable alarmable);
    <T extends Alarmable> void sendAlarms(List<T> alarms);
}
