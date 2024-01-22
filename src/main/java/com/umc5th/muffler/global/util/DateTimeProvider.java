package com.umc5th.muffler.global.util;

import java.time.LocalDate;
import java.util.Date;

public interface DateTimeProvider {
    LocalDate nowDate();
    Date getDateAfterDays(int duration);
}
