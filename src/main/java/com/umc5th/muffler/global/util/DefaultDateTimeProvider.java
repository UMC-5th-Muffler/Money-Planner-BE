package com.umc5th.muffler.global.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DefaultDateTimeProvider implements DateTimeProvider {
    private static final String ZONE_ID = "Asia/Seoul";

    /**
     * @return 서울 시간대의 현재 날짜. {@code LocalDate} 형식으로 반환됩니다.
     */
    @Override
    public LocalDate nowDate() {
        return LocalDate.now(ZoneId.of(ZONE_ID));
    }

    /**
     * @return (서울 시간대의 현재 시각 + duration 일) 후의 {@code Date} 반환
     */
    @Override
    public Date getDateAfterDays(int duration) {
        return Date.from(
                ZonedDateTime.now(ZoneId.of(ZONE_ID))
                        .plus(duration, ChronoUnit.DAYS).toInstant());
    }
}
