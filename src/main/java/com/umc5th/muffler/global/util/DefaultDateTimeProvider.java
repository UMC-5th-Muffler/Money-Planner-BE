package com.umc5th.muffler.global.util;

import java.time.LocalDate;
import java.time.ZoneId;

public class DefaultDateTimeProvider implements DateTimeProvider {
    @Override
    public LocalDate nowDate() {
        return LocalDate.now(ZoneId.of("Asia/Seoul"));
    }
}
