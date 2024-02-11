package com.umc5th.muffler.schedule.service;

import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.global.util.DateTimeProvider;
import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.message.service.AlarmService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduledAlarmService {
    private final DateTimeProvider dateTimeProvider;
    private final DailyPlanRepository dailyPlanRepository;
    private final AlarmService alarmService;

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void alarmTodayPlan() {
        LocalDate today = dateTimeProvider.nowDate();
        List<DailyPlanAlarm> data = dailyPlanRepository.findDailyPlanAlarms(today);
        alarmService.sendDailyAlarms(data);
    }
}
