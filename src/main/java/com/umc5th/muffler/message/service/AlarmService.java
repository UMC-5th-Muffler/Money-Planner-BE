package com.umc5th.muffler.message.service;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import java.util.List;

public interface AlarmService {
    List<String> sendDailyAlarms(List<DailyPlanAlarm> dailyPlanAlarms);
}
