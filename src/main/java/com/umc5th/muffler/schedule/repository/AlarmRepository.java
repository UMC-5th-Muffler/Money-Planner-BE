package com.umc5th.muffler.schedule.repository;

import com.umc5th.muffler.alarm.dto.DailyPlanAlarm;
import java.time.LocalDate;
import java.util.List;

public interface AlarmRepository {
    List<DailyPlanAlarm> findDailyPlanAlarms(LocalDate date);

}
